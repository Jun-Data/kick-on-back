package com.kickon.kickonback.controller;

import com.kickon.kickonback.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 이 클래스는 REST API 컨트롤러임을 표시, 모든 메서드 반환값이 자동으로 JSON으로 변환됨
@RequestMapping// 이 컨트롤러의 기본 URL 경로, 모든 엔드포인트가 /api/teams로 시작
@RequiredArgsConstructor // final 필드 자동 주입
public class TeamController {
    // Service 주입
    private final TeamService teamService;

    // football-data.org에서 4대 리그 데이터 수집
    @PostMapping("/init") // HTTP POST 요청을 받는 엔드포인트
    public ResponseEntity<String> initData() {
        teamService.initData();
        return ResponseEntity.ok("4대 리그 데이터 수집 완료");
    }
}
