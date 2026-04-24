package com.graduation.crm.modules.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class AiRestTemplateConfig {

    private final AiProperties aiProperties;

    /**
     * AI 调用独立设置超时时间，避免模型接口慢响应拖住普通业务接口。
     */
    @Bean
    public RestTemplate aiRestTemplate(RestTemplateBuilder builder) {
        Duration timeout = Duration.ofSeconds(aiProperties.getTimeoutSeconds());
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(timeout)
                .build();
    }
}

