package com.graduation.crm.modules.dashboard.vo;

import lombok.Data;

@Data
public class SimpleCustomerVO {

    private Long id;
    private String customerName;
    private String mobile;
    private String status;
    private String intentLevel;
    private Integer heatScore;
    private String advisorName;
}

