package com.graduation.crm.modules.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConfigUpdateDTO {

    @NotBlank(message = "配置值不能为空")
    private String configValue;
    private String configName;
    private String remark;
}

