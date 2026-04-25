package com.graduation.crm.modules.competitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_competitor_crawl_log")
public class CompetitorCrawlLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long competitorId;
    private String crawlUrl;
    private String crawlStatus;
    private Long snapshotId;
    private String message;
    private String rawPayload;
    private Long createdBy;
    private LocalDateTime createdAt;
}
