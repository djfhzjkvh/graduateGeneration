package com.graduation.crm.modules.job.dto;

import lombok.Data;

@Data
public class JobSettingsUpdateDTO {
    private Integer refreshOverdueEnabled;
    private Integer remindDueSoonEnabled;
    private Integer dailyAutomationEnabled;
    private String dailyAutomationTime;
    private Integer retryFailedNotificationsEnabled;
    private Integer dailyGenerateFollowTasksEnabled;
    private Integer dailyRecalculateHeatEnabled;
    private Integer dailyPredictionSnapshotsEnabled;
    private Integer dailyCompetitorAlertsEnabled;
    private Integer dailyReferralInviteEnabled;
    private Integer dailyHotNoteQuotesEnabled;
    private Integer dailyReportEnabled;
    private Integer dailyDashboardSummaryEnabled;
    private Integer dailyVoiceBriefingEnabled;
    private Integer dailyOpsRiskTasksEnabled;
}
