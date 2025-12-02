package com.kickon.kickonback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// @Configuration = 이 클래스는 Spring 설정 클래스임을 표시
@Configuration
public class RestTemplateConfig {
    // @Bean = 이 메서드가 반환하는 객체를 Spring이 관리하도록 등록
    // 다른 곳에서 RestTemplate이 필요하면 자동으로 주입
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
