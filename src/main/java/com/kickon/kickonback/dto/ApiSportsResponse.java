package com.kickon.kickonback.dto;

import lombok.Data;
import java.util.List;

@Data
public class ApiSportsResponse {
    // JSON 의 response 배열이 여기로 매핑
    private List<TeamDataDto> response;
}
