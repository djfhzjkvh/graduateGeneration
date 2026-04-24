package com.graduation.crm.modules.ai.vo;

import lombok.Data;

@Data
public class ScriptGenerateVO {

    private String scriptText;
    private String modelName;
    private Integer tokenUsage;
}

