package com.graduation.crm.modules.customer.vo;

import lombok.Data;

import java.util.List;

@Data
public class CustomerTimelineVO {
    private Long customerId;
    private String customerName;
    private List<CustomerTimelineItemVO> items;
}
