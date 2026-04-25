package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DashboardVoiceBriefingVO {
    private Long id;
    private LocalDate briefingDate;
    private Long managerId;
    private String briefingText;
    private String keyRisks;
    private String suggestedActions;
    private String audioUrl;
    private String audioStatus;
    private String generationType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
