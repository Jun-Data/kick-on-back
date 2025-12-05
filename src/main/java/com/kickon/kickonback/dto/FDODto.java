package com.kickon.kickonback.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // 여기에 정의 안 된 필드는 전부 무시
public class FDODto {
    // 리그 정보 competition : {...}
    private LeagueData competition;

    // 팀 목록 teams : [...]
    private List<TeamData> teams;

    // 내부 클래스 정의
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LeagueData {
        private Long id; // 2021
        private String name; // "Premier League"
        private String code; // "PL"
        private String emblem; // 리그 엠블럼 URL
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamData {
        private Long id; // 65
        private String name; // "Manchester City FC"
        private String shortName; // "Man City"
        private String tla; // "MCI"
        private String crest; // 로고 URL
        private String address; // 주소
        private String website; // 홈페이지
        private Integer founded; // 창단 연도 1880
        private String venue; // 경기장
        private String clubColors; // 팀 색
        
        private AreaData area; // 소속 국가 정보
        private CoachData coach; // 감독 정보

        private List<PlayerData> squad; // 선수 명단

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AreaData {
            private Long id; // 2072
            private String name; // "England"
            private String code; // "ENG"
            private String flag; // 국기 이미지
        }
        
        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CoachData {
            private Long id; // 11603
            private String name; // "Pep Guardiola"
            private String dateOfBirth; // "1971-01-18"
            private String nationality; // "Spain"
            private ContractData contract; // "contract" : {...}

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ContractData {
                private String start; // "2016-07"
                private String until; // "2027-06"
            }
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PlayerData {
            private Long id; // 7888
            private String name; // "Phil Foden"
            private String position; // "Right Winger"
            private String dateOfBirth; // "2000-05-28"
            private String nationality; // "England"
        }
    }
}