package com.kickon.kickonback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // 이 클래스는 DB 테이블이 된다
@Getter // 모든 필드의 조회 기능 자동 생성
@NoArgsConstructor // 빈 생성자 (JPA 필수)
@AllArgsConstructor // 전체 생성자(Builder 필수)
@Builder // 객체 생성을 쉽게 도와주는 도구
public class Team {
    @Id // PK(고유 식별자) - API Sports에서 주는 팀 ID를 그대로 사용
    private Long id;

    private String name; // 팀 이름
    private String code; // 팀 약어
    private String logoUrl; // 팀 로고 이미지 주소
    private Integer founded; // 창단 연도

    private String venueName; // 홈 구장 이름
    private String venueCity; // 연고지 (도시)

    // 추후 리그 정보 추가
}
