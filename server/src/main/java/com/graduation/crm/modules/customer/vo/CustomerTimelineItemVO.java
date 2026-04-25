package com.graduation.crm.modules.customer.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerTimelineItemVO {
    private String eventType;
    private String eventTitle;
    private String eventContent;
    private Long bizId;
    private String bizType;
    private Long operatorId;
    private LocalDateTime occurredAt;
}
