package com.kickon.kickonback.repository;

import com.kickon.kickonback.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // 특정 리그 코드를 가진 팀 목록 조회
    // Team 엔티티 안의 'league'객체 안의 'code' 필드를 확인
    // SQL : SELECT * FROM team t JOIN league l ON t.league_id WHERE l.code=?
    List<Team> findAllByLeague_Code(String leagueCode);
}
