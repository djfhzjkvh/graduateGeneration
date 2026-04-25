package com.graduation.crm.modules.competitor.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CompetitorAlertVO {
    private Long competitorId;
    private String competitorName;
    private String alertType;
    private String title;
    private String content;
    private LocalDate alertDate;
}
