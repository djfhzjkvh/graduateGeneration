package com.graduation.crm.modules.customer.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerListVO {

    private Long id;
    private String customerName;
    private String mobile;
    private String status;
    private String intentLevel;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String region;
    private String houseType;
    private Integer heatScore;
    private String advisorName;
    private String managerName;
    private LocalDateTime latestFollowTime;
    private LocalDateTime nextFollowTime;
}

