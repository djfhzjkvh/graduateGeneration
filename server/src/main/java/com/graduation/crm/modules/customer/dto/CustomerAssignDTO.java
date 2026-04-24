package com.graduation.crm.modules.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 客户分配/转移请求参数。
 */
@Data
public class CustomerAssignDTO {

    @NotNull(message = "接收顾问不能为空")
    private Long toUserId;

    private Long managerId;
    private Long deptId;
    private String actionType;
    private String remark;
    private Long createdBy;
}
