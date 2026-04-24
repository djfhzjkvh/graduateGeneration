package com.graduation.crm.modules.heat.vo;

import lombok.Data;

@Data
public class HighIntentCustomerVO {

    private Long customerId;
    private String customerName;
    private String mobile;
    private String status;
    private String intentLevel;
    private Integer heatScore;
    private String advisorName;
    private String managerName;
}

