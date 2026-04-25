package com.graduation.crm.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_script_template")
public class AiScriptTemplate {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String templateName;
    private String sceneType;
    private String channelType;
    private String customerTag;
    private String personalityType;
    private String content;
    private Integer isDefault;
    private Integer status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
