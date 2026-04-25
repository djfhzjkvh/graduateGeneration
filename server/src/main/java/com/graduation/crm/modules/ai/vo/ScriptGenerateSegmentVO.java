package com.graduation.crm.modules.ai.vo;

import lombok.Data;

@Data
public class ScriptGenerateSegmentVO {
    private String channelType;
    private String title;
    private String content;
    private Long templateId;
}
