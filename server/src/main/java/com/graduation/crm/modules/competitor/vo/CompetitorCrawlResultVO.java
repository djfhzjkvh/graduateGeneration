package com.graduation.crm.modules.competitor.vo;

import lombok.Data;

@Data
public class CompetitorCrawlResultVO {
    private Integer totalCount;
    private Integer successCount;
    private Integer failedCount;
    private String message;
}
