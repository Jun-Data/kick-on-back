package com.kickon.kickonback.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    private Long id; // football-data.org의 선수 ID

    // 기본 정보 football-data.org 출처
    @Column(nullable = false)
    private String name; // 이름 "Erling Haaland"

    private String position; // 포지션 "Centre-Forward"
    private String dateOfBirth; // 생년월일 "2000-07-21"
    private String nationality; // 국적 "Norway"
    private Integer shirtNumber; // 등번호 "9"

    // 이미지 & 상세 정보 - TheSportsDB 출처
    @Column(length = 2000) // 긴 설명
    private String description; // 선수 설명 (BIO)

    private String height; // 키
    private String weight; // 몸무게
    private String wage; // 주급

    private String profileImgUrl; // 컷아웃(투명 배경) 이미지
    private String faceImgUrl; // 증명 사진

    // 관계 설정
    @ManyToOne(fetch = FetchType.LAZY) // 선수 정보 볼 때 팀 정보까지 굳이 안가져옴
    @JoinColumn(name="team_id") // DB에는 'team_id'로 저장
    private Team team;

}
