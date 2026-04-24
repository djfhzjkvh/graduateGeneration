package com.graduation.crm.modules.admin.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AiConfigVO {

    private Long id;
    private String providerName;
    private String modelName;
    private String apiUrl;
    private Boolean apiKeyConfigured;
    private BigDecimal temperature;
    private Integer status;
    private LocalDateTime updatedAt;
}

