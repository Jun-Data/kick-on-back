package com.kickon.kickonback.service;

import com.kickon.kickonback.dto.TSDBEventsDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TeamNameMatcher {
    private final RestTemplate restTemplate;
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    // { "PL" : ["Manchester City", ...] } 형태의 리그별 이름 캐시
    private final Map<String, Set<String>> leagueTeamNamesCache = new HashMap<>();

    // 1. TSDB 리그 ID로 일정 API를 호출해서 정답지(팀 이름 목록)을 만듬
    public void loadTsdbTeamNames(String leagueCode, String tsdbLeagueId) {
        String currentSeason = getCurrentSeasonStr(); // 시즌 자동 계산
        String url = "https://www.thesportsdb.com/api/v1/json/123/eventsseason.php?id=" + tsdbLeagueId + "&s=" + currentSeason;

        try {
            var response = restTemplate.getForObject(url, TSDBEventsDto.class);
            Set<String> names = new HashSet<>();

            if(response!=null && response.getEvents() != null) {
                for (var event : response.getEvents()) {
                    names.add(event.getStrHomeTeam());
                    names.add(event.getStrAwayTeam());
                }
            }
            leagueTeamNamesCache.put(leagueCode, names);
            System.out.println(" [" + leagueCode + "] 매칭용 정답지 확보 완료 (" + names.size() + "팀)");
        } catch (Exception e) {
            System.out.println(" 정답지 로딩 실패 (" + leagueCode + "): " + e.getMessage());
        }
    }

    // 2. FDO 이름과 가장 유사한 TSDB 이름을 찾는다
    public String findBestMatch(String leagueCode, String fdoName) {
    Set<String> candidates = leagueTeamNamesCache.get(leagueCode);
    if (candidates == null || candidates.isEmpty()) return null;

    String bestMatch = null;
    double maxScore = 0.0;

    // FDO 이름 정제 (비교 정확도를 위해 불필요 단어 제거)
    String cleanFdoName = fdoName
            .replace(" FC", "").replace(" AFC", "").replace(" CF", "")
            .replace("1. ", "").replace("RC ", "").replace("UD ", "")
            .replace("TSG 1899 ", "").replace(" de ", " ")
            .trim();

    for (String tsdbName : candidates) {
        double score = similarity.apply(cleanFdoName, tsdbName);
        if (score > maxScore) {
            maxScore = score;
            bestMatch = tsdbName;
        }
    }
    // 유사도가 0.8 (80%) 이상일 때만 인정 (너무 다르면 null)
    return (maxScore > 0.7) ? bestMatch : null;
    }
    // 📅 현재 날짜 기준으로 시즌 문자열 계산 (예: 2025-2026)
    private String getCurrentSeasonStr() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        // 7월부터 새 시즌으로 간주
        if (month >= 7) {
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }
}