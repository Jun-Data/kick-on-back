package com.kickon.kickonback.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TSDBTeamDto {
    private List<TeamDetail> teams;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamDetail {
        // 이름 매칭 용
        private String strTeam; // 팀 이름

        // 이미지 정보
        private String strEquipment; // 유니폼

        // 소셜 미디어 & 웹사이트
        private String strYoutube; // 유튜브 사이트
        private String strFacebook; // 페이스북 사이트
        private String strTwitter; // 트위터 사이트
        private String strInstagram; // 인스타 사이트

        // 추가 정보
        private String intStadiumCapacity; // 경기장 수용 관중수
        private String strDescriptionEN; // 팀 설명
    }
}
