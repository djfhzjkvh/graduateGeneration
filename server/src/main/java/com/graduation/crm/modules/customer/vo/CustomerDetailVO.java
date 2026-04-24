package com.graduation.crm.modules.customer.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CustomerDetailVO {

    private Long id;
    private String customerName;
    private String mobile;
    private String gender;
    private Integer age;
    private String source;
    private String status;
    private String intentLevel;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String region;
    private String houseType;
    private String purpose;
    private String remark;
    private Integer heatScore;
    private Long advisorId;
    private String advisorName;
    private Long managerId;
    private String managerName;
    private LocalDateTime latestFollowTime;
    private LocalDateTime nextFollowTime;
    private List<String> tagNames;
}

