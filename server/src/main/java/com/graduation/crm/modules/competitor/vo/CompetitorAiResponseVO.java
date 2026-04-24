package com.graduation.crm.modules.competitor.vo;

import lombok.Data;

@Data
public class CompetitorAiResponseVO {

    private String responseText;
    private String modelName;
    private Integer tokenUsage;
}

