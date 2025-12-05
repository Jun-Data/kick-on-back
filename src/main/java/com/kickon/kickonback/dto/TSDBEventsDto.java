package com.kickon.kickonback.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TSDBEventsDto {
    private List<EventData> events;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventData {
        private String strHomeTeam; // 홈팀 이름
        private String strAwayTeam; // 원정팀 이름
    }
}