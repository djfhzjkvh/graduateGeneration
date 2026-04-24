package com.graduation.crm.modules.ai.vo;

import lombok.Data;

@Data
public class AiChatVO {

    private String content;
    private String modelName;
    private Integer tokenUsage;
}

