package com.graduation.crm.modules.customer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomerUpdateDTO {

    private String customerName;
    private String mobile;
    private String gender;
    private Integer age;
    private String source;
    private String status;
    private Long advisorId;
    private Long managerId;
    private Long deptId;
    private String intentLevel;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String region;
    private String houseType;
    private String purpose;
    private String remark;
    private List<Long> tagIds;
}

