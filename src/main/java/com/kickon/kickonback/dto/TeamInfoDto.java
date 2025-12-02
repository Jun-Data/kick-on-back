package com.kickon.kickonback.dto;

import lombok.Data;

// @Data = Lombok이 자동으로 getter, setter, toString 등을 만들어줌
// TypeScript의 interface 처럼 데이터 구조만 정의

@Data
public class TeamInfoDto {
    private Long id;  // 팀 ID
    private String name; // 팀 이름
    private String code; // 팀 약어
    private String logo; // 팀 로고 이미지 주소
    private Integer founded; // 창단 연도
}
