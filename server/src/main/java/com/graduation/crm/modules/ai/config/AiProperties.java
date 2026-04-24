package com.graduation.crm.modules.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private String provider;
    private String modelName;
    private String baseUrl;
    private String apiKey;
    private BigDecimal temperature = new BigDecimal("0.7");
    private Integer maxTokens = 2000;
    private BigDecimal topP = new BigDecimal("0.9");
    private Integer timeoutSeconds = 60;
    private Boolean saveCallLog = true;
}

