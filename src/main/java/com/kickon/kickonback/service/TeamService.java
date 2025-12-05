package com.kickon.kickonback.service;

import com.kickon.kickonback.dto.FDODto;
import com.kickon.kickonback.entity.League;
import com.kickon.kickonback.entity.Player;
import com.kickon.kickonback.entity.Team;
import com.kickon.kickonback.repository.LeagueRepository;
import com.kickon.kickonback.repository.PlayerRepository;
import com.kickon.kickonback.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.kickon.kickonback.dto.TSDBTeamDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service // 이 클래스는 Service 레이어임을 표시
@RequiredArgsConstructor // final 필드를 자동으로 생성자 주입해줌 (Lombok)
public class TeamService {
    // TSDB league ID 매핑
    private static final Map<String, String> LEAGUE_TSDB_IDS = Map.of("PL", "4328", "PD", "4335", "BL1", "4331", "SA", "4332");
    // 의존성 주입 (DI) - Spring이 자동으로 넣어줌
    private final RestTemplate restTemplate;
    private final LeagueRepository leagueRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final TeamNameMatcher teamNameMatcher;

    // @Value : application.properties의 값을 가져옴
    @Value("${api.fdo.url}")
    private String apiUrl;

    @Value("${api.fdo.key}")
    private String apiKey;

    // [1] 4대 리그 데이터를 순서대로 수집하는 메인 실행 함수
    public void initData() {
        // TheSportsDB 매칭을 위한 리그 ID 맵
        Map<String, String> leagueIdMap = Map.of(
                "PL", "4328", "PD", "4335", "BL1", "4331", "SA", "4332"
        );
        for (String code : leagueIdMap.keySet()) {
            {
                System.out.println(code + "프로세스 시작");
                try {
                    // 1. 기본 정보 수집 (football-data.org)
                    fetchAndSaveLeagueData(code);
                    // 2. TheSportsDB 이름 로딩
                    teamNameMatcher.loadTsdbTeamNames(code, leagueIdMap.get(code));
                    // 3. TheSportsDB 상세 정보 보강
                    enrichTeamDetails(code);

                    System.out.println("다음 리그를 위해 5초 대기");
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.out.println("Football-data.org" + e.getMessage());
                }
            }
            System.out.println("모든 데이터 수집 완료");

        }
    }

    // [1-1] FDO 데이터 수집
    @Transactional // 도중에 에러나면 저장했던 것 취소 (롤백)
    public void fetchAndSaveLeagueData(String leagueCode) {
        // 1. API 호출 준비 (헤더 + URL)
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = String.format("%s/competitions/%s/teams", apiUrl, leagueCode);

        // 2. API 찌르기 (DTO로 받기)
        ResponseEntity<FDODto> response = restTemplate.exchange(url, HttpMethod.GET, entity, FDODto.class);

        FDODto dto = response.getBody();
        if(dto == null) return;

        // 국가 정보 추출
        String countryName = "Unknown";
        String countryFlag = null;

        if (dto.getTeams() !=null && !dto.getTeams().isEmpty()) {
            FDODto.TeamData.AreaData area = dto.getTeams().get(0).getArea();
            if(area != null) {
                countryName = area.getName();
                countryFlag = area.getFlag();
            }
        }

        // 3. League 저장
        League league = League.builder()
                .id(dto.getCompetition().getId())
                .name(dto.getCompetition().getName())
                .code(dto.getCompetition().getCode())
                .emblemUrl(dto.getCompetition().getEmblem())
                .country(countryName)
                .flagUrl(countryFlag)
                .build();
        leagueRepository.save(league); // DB에 저장

        // 4. Team 저장
        if (dto.getTeams() !=null) {
            for(FDODto.TeamData tData : dto.getTeams()) {
                Team team = Team.builder()
                        .id(tData.getId())
                        .name(tData.getName())
                        .shortName(tData.getShortName())
                        .tla(tData.getTla())
                        .crest(tData.getCrest())
                        .address(tData.getAddress())
                        .website(tData.getWebsite())
                        .founded(tData.getFounded())
                        .clubColors(tData.getClubColors())
                        .venue(tData.getVenue())
                        .league(league) // 부모(League)와 연결!
                        .build();

                teamRepository.save(team); // DB에 저장

                // 5. Player 저장
                if (tData.getSquad() !=null) {
                    savePlayers(tData.getSquad(), team);
                }
            }
        }
    }

    // 선수 저장 로직
    private void savePlayers(List<FDODto.TeamData.PlayerData> squad, Team team) {
        for (FDODto.TeamData.PlayerData pData : squad) {
            Player player = Player.builder()
                    .id(pData.getId())
                    .name(pData.getName())
                    .position(pData.getPosition())
                    .dateOfBirth(pData.getDateOfBirth())
                    .nationality(pData.getNationality())
                    .team(team) // 부모(Team)와 연결
                    .build();

            playerRepository.save(player);
        }
    }

    // TheSportsDB 데이터 채우는 메서드
    public void enrichTeamDetails(String leagueCode) {
        // 해당 리그의 팀을 가져옴
        List<Team> teams = teamRepository.findAllByLeague_Code(leagueCode);

        // TheSportsDB 검색 API
        String TSDB_URL = "https://www.thesportsdb.com/api/v1/json/123/searchteams.php?t=%s";

        for (Team team : teams) {
            try {
                // Football-data.org 이름 -> TheSportsDB 이름에서 찾기
                String tsdbName = teamNameMatcher.findBestMatch(leagueCode, team.getName());

                // null 체크
                if (tsdbName == null) {
                    System.out.println("매칭 실패: " +
                            team.getName());
                    continue;  // 다음 팀으로
                }

                // TheSportsDB API 호출
                String encodedName = URLEncoder.encode(tsdbName, StandardCharsets.UTF_8);
                String url = String.format(TSDB_URL, encodedName);
                TSDBTeamDto response = restTemplate.getForObject(url, TSDBTeamDto.class);

                // 데이터 업데이트
                if (response != null && response.getTeams() != null && !response.getTeams().isEmpty()) {
                    var detailData = response.getTeams().get(0); // 첫번째 검색 결과

                    // 엔티티 업데이트 (Setter 사용)
                    team.setJerseyUrl(detailData.getStrEquipment());
                    team.setYoutubeUrl(detailData.getStrYoutube());
                    team.setFacebookUrl(detailData.getStrFacebook());
                    team.setTwitterUrl(detailData.getStrTwitter());
                    team.setInstagramUrl(detailData.getStrInstagram());
                    team.setDescription(detailData.getStrDescriptionEN());

                    teamRepository.save(team); // DB에 저장

                    System.out.println("TheSportsDB 데이터 업데이트 성공");
                }
                Thread.sleep(1500);

            } catch (Exception e) {
                System.out.println("TheSportsDB 처리 중 에러" + team.getName() + e.getMessage());
            }
        }
    }

    // 이름 정제 헬퍼 메서드
    private String normalizeName(String originalName) {
        return originalName
                .replace(" FC", "")
                .replace(" AFC", "")
                .replace("1. ","")
                .replace("FSV ", "")
                .replace("TSG 1899 ", "")
                .replace("UD ", "")
                .replace("RC ", "")
                .replace(" Fútbol", "")  // ← 추가
                .replace("&", "and")     // ← 추가
                .trim();
    }
}