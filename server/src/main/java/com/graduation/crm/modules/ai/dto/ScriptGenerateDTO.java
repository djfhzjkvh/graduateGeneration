package com.graduation.crm.modules.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ScriptGenerateDTO {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "场景类型不能为空")
    private String sceneType;

    @NotBlank(message = "渠道类型不能为空")
    private String channelType;

    private String customPrompt;
}

