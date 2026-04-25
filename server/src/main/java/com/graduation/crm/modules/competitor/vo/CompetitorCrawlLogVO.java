package com.graduation.crm.modules.competitor.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompetitorCrawlLogVO {
    private Long id;
    private Long competitorId;
    private String competitorName;
    private String crawlUrl;
    private String crawlStatus;
    private Long snapshotId;
    private String message;
    private String rawPayload;
    private Long createdBy;
    private LocalDateTime createdAt;
}
