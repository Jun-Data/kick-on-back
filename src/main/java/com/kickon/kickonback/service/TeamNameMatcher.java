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

            System.out.println("\n[" + leagueCode + "] TSDB 정답지:");
            names.stream().sorted().forEach(name -> System.out.println("  " + name));

        } catch (Exception e) {
            System.out.println("[" + leagueCode + "] 정답지 로딩 실패: " + e.getMessage());
        }
    }

    // 2. FDO 이름과 가장 유사한 TSDB 이름을 찾는다
    public String findBestMatch(String leagueCode, String fdoName) {
    Set<String> candidates = leagueTeamNamesCache.get(leagueCode);
    if (candidates == null || candidates.isEmpty()) return null;

    String bestMatch = null;
    double maxScore = 0.0;

    // FDO 이름 정규화
    String cleanFdoName = normalizeName(fdoName);

    for (String tsdbName : candidates) {
        // TSDB 이름도 정규화해서 비교
        String cleanTsdbName = normalizeName(tsdbName);
        double score = similarity.apply(cleanFdoName, cleanTsdbName);
        if (score > maxScore) {
            maxScore = score;
            bestMatch = tsdbName;  // 원본 이름 반환
        }
    }

    // 0.8 이상만 매칭 인정
    if (maxScore >= 0.8) {
        System.out.println("  ✓ " + fdoName + " → " + bestMatch + " (" + String.format("%.2f", maxScore) + ")");
        return bestMatch;
    } else {
        System.out.println("  ✗ " + fdoName + " (최고: " + bestMatch + " " + String.format("%.2f", maxScore) + ")");
        return null;
    }
    }

    // 3. 매칭용 이름 정규화 (접두사/접미사/특수문자 제거)
    private String normalizeName(String name) {
        if (name == null) return "";

        return name
            // 접두사 제거 (정규표현식: 문자열 시작 부분)
            .replaceAll("^(AC |FC |AS |SC |1\\. FC |1\\. |RC |UD |TSG 1899 |Borussia |Club |Deportivo )", "")
            // 접미사 제거
            .replace(" FC", "").replace(" AFC", "").replace(" CF", "")
            .replace(" AC", "").replace(" SC", "")
            // 중간 단어 제거
            .replace(" de ", " ").replace(" di ", " ")
            // 특수문자 정규화 (악센트, 움라우트)
            .replace("é", "e").replace("á", "a").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("ö", "o").replace("ü", "u").replace("ä", "a")
            .replace("ß", "ss").replace("ć", "c").replace("ø", "o")
            // 여러 공백을 하나로
            .replaceAll("\\s+", " ")
            .trim();
    }

    // 4. TSDB 검색 API용 이름 정규화 (악센트 제거)
    public String normalizeForSearch(String name) {
        if (name == null) return "";

        return name
            .replace("é", "e").replace("á", "a").replace("í", "i")
            .replace("ó", "o").replace("ú", "u").replace("ñ", "n")
            .replace("ö", "o").replace("ü", "u").replace("ä", "a")
            .replace("ß", "ss").replace("ć", "c").replace("ø", "o")
            .trim();
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