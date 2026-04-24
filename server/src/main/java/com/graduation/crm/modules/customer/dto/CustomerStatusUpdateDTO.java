package com.graduation.crm.modules.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 客户状态流转请求参数。
 */
@Data
public class CustomerStatusUpdateDTO {

    @NotBlank(message = "客户状态不能为空")
    private String status;

    private String remark;
    private Long operatorId;
}
