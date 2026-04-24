package com.graduation.crm.modules.ai.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeadExtractVO {

    private Long extractId;
    private String customerName;
    private String mobile;
    private String gender;
    private String source;
    private String intentLevel;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String region;
    private String houseType;
    private String purpose;
    private String visitTime;
    private String remark;
    private String suggestion;
    private String rawModelOutput;
}

