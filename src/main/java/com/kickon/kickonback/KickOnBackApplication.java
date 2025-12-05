package com.kickon.kickonback;

import com.kickon.kickonback.service.TeamService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class KickOnBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(KickOnBackApplication.class, args);
    }

    // 서버가 켜지면(Run) 해당 코드가 한번 자동으로 실행
    @Bean
    public CommandLineRunner initData(TeamService teamService) {
        return args -> {
            System.out.println("=================================");
            System.out.println("🚀 [START] 데이터 수집을 시작합니다...");

            teamService.initData(); // 👈 여기서 서비스의 모터를 켭니다!

            System.out.println("🏁 [END] 데이터 수집이 완료되었습니다!");
            System.out.println("=================================");
        };
    }
}
