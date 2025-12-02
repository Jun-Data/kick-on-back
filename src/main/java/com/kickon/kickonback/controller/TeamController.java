package com.kickon.kickonback.controller;

import com.kickon.kickonback.entity.Team;
import com.kickon.kickonback.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// @RestController = 이 클래스는 REST API 컨트롤러임을 표시
// 모든 메서드 반환값이 자동으로 JSON으로 반환됨

// @RequestMapping = 이 컨트롤러의 기본 URL 경로
// 모든 엔드포인트가 /api/teams 로 시작

// @RequiredArgsConstructor = final 필드 자동 주입

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TeamController {
    // Service 주입
    private final TeamService teamService;

    // API Sports에서 팀 데이터를 가져와 DB에 저장
    @GetMapping("/fetch")
    public ResponseEntity<String> fetchTeams(
            @RequestParam(defaultValue = "39") int leagueId,
            @RequestParam(defaultValue = "2024") int season
    ) {
        // Service에게 일 시키기
        teamService.fetchAndSaveTeams(leagueId, season);

        // 성공 응답 (HTTP 200 + 메세지)
        return ResponseEntity.ok(
                String.format("리그 %d의 %d 시즌 팀 데이터 저장 완료", leagueId, season)
        );
    }

    // DB에 저장된 모든 팀 조회
    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamService.getAllTeams();

        // 팀 리스트를 자동으로 JSON으로 변환해서 반환
        return ResponseEntity.ok(teams);
    }

    // 특정 ID의 팀 조회
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        // findById()는 Optional을 반환 (값이 없을수도 있음)
        return teamService.getAllTeams().stream()
                .filter(team -> team.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok) // 찾으면 200 OK
                .orElse(ResponseEntity.notFound().build()); // 못 찾으면 404
    }
}
