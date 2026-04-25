package com.graduation.crm.modules.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class ScriptGenerateV2VO {
    private String personalityType;
    private String priceSensitiveLevel;
    private String strategySummary;
    private List<ScriptGenerateSegmentVO> segments;
    private String rawModelOutput;
    private String modelName;
    private Integer tokenUsage;
}
