package com.graduation.crm.modules.ai.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 线索抽取记录返回对象。
 */
@Data
public class LeadExtractRecordVO {

    private Long id;
    private String sourceType;
    private Long sourceFileId;
    private String rawText;
    private String extractJson;
    private String suggestionJson;
    private String confirmStatus;
    private Long customerId;
    private String modelName;
    private String promptVersion;
    private Long createdBy;
    private LocalDateTime createdAt;
}
