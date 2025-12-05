package com.kickon.kickonback.repository;

import com.kickon.kickonback.entity.Player;
import com.kickon.kickonback.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long>{
    // 특정 팀 ID에 소속된 선수 목록 조회
    // SQL: SELECT * FROM player WHERE team_id = ?
    List<Player> findByTeamId(Long teamId);
}
