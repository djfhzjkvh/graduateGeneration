package com.graduation.crm.modules.ai.dto;

import lombok.Data;

/**
 * AI 线索抽取记录查询参数。
 */
@Data
public class LeadExtractRecordQueryDTO {

    private String sourceType;
    private String confirmStatus;
    private Long createdBy;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
