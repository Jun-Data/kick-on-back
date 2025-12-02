package com.kickon.kickonback.service;

import com.kickon.kickonback.dto.ApiSportsResponse;
import com.kickon.kickonback.dto.TeamDataDto;
import com.kickon.kickonback.entity.Team;
import com.kickon.kickonback.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// @Service : 이 클래스는 Service 레이어임을 표시
// @RequiredArgsConstructor : final 필드를 자동으로 생성자 주입해줌 (Lombok)
@Service
@RequiredArgsConstructor
public class TeamService {
    // 의존성 주입 (DI) - Spring이 자동으로 넣어줌
    private final RestTemplate restTemplate; // RestTemplate 주입
    private final TeamRepository teamRepository; // Repository 주입

    // @Value : application.properties의 값을 가져옴
    @Value("${api.sports.url}")
    private String apiUrl; // https://v3.football.api-sports.io

    @Value("${api.sports.key}")
    private String apiKey; // YOUR_API_KEY

    // API Sports에서 EPL 팀 데이터 가져와 DB에 저장
    public void fetchAndSaveTeams(int leagueId, int season) {
        // 1. HTTP 헤더 설정 (axios의 헤더와 동일)
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-apisports-key", apiKey); // API 키 헤더 추가

        // HttpEntity : 요청 본문 + 헤더를 담는 객체
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. API URL 생성
        String url = String.format("%s/teams?league=%d&season=%d", apiUrl, leagueId, season);

        // 3. API 호출 (axios get과 동일)
        ResponseEntity<ApiSportsResponse> response = restTemplate.exchange(
                url, // 요청 URL
                HttpMethod.GET, // HTTP 메서드
                entity, // 헤더
                ApiSportsResponse.class // 응답을 이 클래스로 변환
        );

        // 4. 응답 데이터 가져오기
        ApiSportsResponse apiResponse = response.getBody();

        // 5. null 체크 (안정성)
        if (apiResponse == null || apiResponse.getResponse() == null ) {
            throw new RuntimeException("API 응답이 비어있습니다");
        }

        // 6. DTO -> Entity 변환 후 DB 저장
        List<Team> teams = apiResponse.getResponse().stream().map(this::convertToEntity).toList();

        // 7. DB에 저장 (saveAll : 여러 개 한번에 저장)
        teamRepository.saveAll(teams);
    }
    // DTO를 Entity로 변환하는 헬퍼 메서드
    private Team convertToEntity(TeamDataDto dto) {
        return Team.builder()
                .id(dto.getTeam().getId())
                .name(dto.getTeam().getName())
                .code(dto.getTeam().getCode())
                .logoUrl(dto.getTeam().getLogo())       // logo → logoUrl
                .founded(dto.getTeam().getFounded())
                .venueName(dto.getVenue().getName())
                .venueCity(dto.getVenue().getCity())
                .build();
    }

    // DB에 저장된 모든 팀 조회
    public List<Team> getAllTeams() {
        return teamRepository.findAll(); // SELECT * FROM team
    }
}
