package com.graduation.crm.modules.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CustomerTagCreateDTO {

    @NotBlank(message = "标签名称不能为空")
    private String tagName;
    private String tagType;
    private String color;
}

