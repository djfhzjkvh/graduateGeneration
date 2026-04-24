package com.graduation.crm.modules.customer.dto;

import lombok.Data;

/**
 * 客户分配日志查询参数。
 */
@Data
public class CustomerAssignLogQueryDTO {

    private Long customerId;
    private Long toUserId;
    private String actionType;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
