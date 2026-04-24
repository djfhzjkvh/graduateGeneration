package com.graduation.crm.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_lead_extract_record")
public class LeadExtractRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String sourceType;
    private Long sourceFileId;
    private String rawText;
    private String cleanedText;
    private String extractJson;
    private String suggestionJson;
    private String confirmStatus;
    private Long customerId;
    private String modelName;
    private String promptVersion;
    private Long createdBy;
    private LocalDateTime createdAt;
}

