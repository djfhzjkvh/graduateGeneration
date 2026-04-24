package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

import java.util.List;

@Data
public class ManagerDashboardVO {

    private Integer teamCustomerCount;
    private Integer teamPendingTaskCount;
    private Integer teamOverdueTaskCount;
    private Integer teamHighIntentCustomerCount;
    private List<AdvisorRankVO> advisorRankList;
}

