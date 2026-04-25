package com.graduation.crm.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_script_generate_log")
public class AiScriptGenerateLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private String sceneType;
    private String channelType;
    private String inputJson;
    private String resultText;
    private String modelName;
    private String promptVersion;
    private Long createdBy;
    private LocalDateTime createdAt;
}
