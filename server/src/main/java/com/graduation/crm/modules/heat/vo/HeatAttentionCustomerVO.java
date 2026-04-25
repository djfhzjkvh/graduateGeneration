package com.graduation.crm.modules.heat.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HeatAttentionCustomerVO {
    private Long customerId;
    private String customerName;
    private String mobile;
    private Integer heatScore;
    private String intentLevel;
    private Long advisorId;
    private String advisorName;
    private BigDecimal budgetMax;
    private String reason;
}
