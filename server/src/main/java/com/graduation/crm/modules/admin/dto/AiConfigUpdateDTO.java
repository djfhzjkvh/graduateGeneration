package com.graduation.crm.modules.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class AiConfigUpdateDTO {

    @NotBlank(message = "模型供应商不能为空")
    private String providerName;

    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    @NotBlank(message = "接口地址不能为空")
    private String apiUrl;

    private String apiKey;
    private BigDecimal temperature;
    private Integer status;
}

