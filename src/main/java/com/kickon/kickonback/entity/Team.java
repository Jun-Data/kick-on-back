package com.kickon.kickonback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Entity // 이 클래스는 DB의 team 테이블이 된다
@Getter // 모든 필드의 조회 기능 자동 생성 team.getName()
@Setter // 나중에 이미지를 채워 넣기 위해 수정 기능 허용
@NoArgsConstructor // 기본 생성자 (JPA 필수)
@AllArgsConstructor // 전체 생성자 (Builder 필수)
@Builder // 객체 생성을 도와주는 도구
public class Team {
    @Id // 테이블의 PK(고유 식별자) - football-data.org의 ID
    private Long id;

    // 기본 정보 - football-data.org 출처
    private String name; // 팀 긴 이름
    private String shortName; // 팀 짧은 이름
    private String tla; // 팀 약어
    private String crest; // 팀 로고 이미지
    private String address; // 경기장 주소
    private String website; // 팀 웹사이트
    private Integer founded; // 창단 연도
    private String clubColors; // 팀 색
    private String venue; // 경기장

    // 추가 정보 - TheSportsDB 출처
    private String jerseyUrl; // 유니폼
    private String youtubeUrl; // 유튜브 사이트
    private String facebookUrl; // 페이스북 사이트
    private String twitterUrl; // 트위터 사이트
    private String instagramUrl; // 인스타 사이트
    @Column(columnDefinition = "TEXT")
    private String description; // 팀 설명

    // 관계 설정
    @ManyToOne(fetch = FetchType.LAZY) // 필요할때만 리그 정보 가져옴
    @JoinColumn(name = "league_id") // DB에는 'league_id'라는 이름으로 저장
    private League league;

    @OneToMany(mappedBy = "team") // Player의 team 필드와 연결
    private List<Player> players; // 이 팀에 속한 선수들
}
