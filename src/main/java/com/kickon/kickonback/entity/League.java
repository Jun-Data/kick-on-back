package com.kickon.kickonback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity // 이 클래스를 DB 테이블로
@Getter // getter 자동 생성 (Lombok)
@NoArgsConstructor // 기본 생성자 (JPA 필수)
@AllArgsConstructor // 전체 생성자 (Builder 필요)
@Builder // 빌더 패턴

public class League {
    @Id // football-data.org의 리그 ID
    private Long id; // API의 competition.id (2021, 2014, 2002, 2019)

    private String name; // 리그 명 "Premier League"
    private String code; // 리그 코드 "PL"
    private String country; // 국가 "England"
    private String flagUrl; // 국기 이미지
    private String emblemUrl; // 엠블럼 이미지

    // 관계 설정
    @OneToMany(mappedBy = "league") // Team의 league 필드와 연결
    private List<Team> teams; // 이 리그에 속한 팀들

}
