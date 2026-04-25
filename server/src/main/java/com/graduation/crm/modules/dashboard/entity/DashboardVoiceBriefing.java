package com.graduation.crm.modules.dashboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("dashboard_voice_briefing")
public class DashboardVoiceBriefing {
    @TableId(type = IdType.AUTO)
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
