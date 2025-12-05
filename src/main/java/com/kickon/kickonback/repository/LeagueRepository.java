package com.kickon.kickonback.repository;

import com.kickon.kickonback.entity.League;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// <관리할 엔티티, PK의 타입>
public interface LeagueRepository extends JpaRepository<League, Long>{
    Optional<League> findByCode(String code);
}
