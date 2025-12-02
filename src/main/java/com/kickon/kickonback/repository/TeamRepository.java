package com.kickon.kickonback.repository;

import com.kickon.kickonback.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<엔티티타입, ID타입> 만 작성하면 Spring이 자동으로 CRUD 메서드 만들어줌
// JpaRepository를 상속받는 것만으로도 save(), findAll(), findById() 같은 기능이 자동으로 생성
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    // 자동으로 제공되는 메서드들:
    // save(Team team)           → INSERT 또는 UPDATE
    // findById(Long id)         → SELECT * WHERE id = ?
    // findAll()                 → SELECT * FROM team
    // deleteById(Long id)       → DELETE WHERE id = ?
    // count()                   → SELECT COUNT(*)

    // 추가로 필요한 메서드가 있으면 여기에 선언만 하면 됨
    // 예: findByName(String name) → Spring이 자동으로 SQL 생성

}
