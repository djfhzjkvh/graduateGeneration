package com.graduation.crm.modules.competitor.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerCompetitorFocusVO {

    private Long id;
    private Long customerId;
    private Long competitorId;
    private String competitorName;
    private String focusContent;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
}

