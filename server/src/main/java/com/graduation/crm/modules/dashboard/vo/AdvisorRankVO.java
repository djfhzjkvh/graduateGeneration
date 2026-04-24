package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

@Data
public class AdvisorRankVO {

    private Long advisorId;
    private String advisorName;
    private Integer customerCount;
    private Integer highIntentCustomerCount;
    private Integer pendingTaskCount;
    private Integer dealCustomerCount;
}

