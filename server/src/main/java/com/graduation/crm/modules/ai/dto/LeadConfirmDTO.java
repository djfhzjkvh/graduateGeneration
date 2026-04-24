package com.graduation.crm.modules.ai.dto;

import com.graduation.crm.modules.customer.dto.CustomerCreateDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class LeadConfirmDTO {

    @NotNull(message = "抽取记录ID不能为空")
    private Long extractId;

    @Valid
    @NotNull(message = "客户表单不能为空")
    private CustomerCreateDTO customerForm;
}

