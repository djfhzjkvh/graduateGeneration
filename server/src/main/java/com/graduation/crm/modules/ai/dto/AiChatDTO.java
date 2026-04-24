package com.graduation.crm.modules.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AiChatDTO {

    @NotBlank(message = "提示词不能为空")
    private String prompt;
    private Long bizId;
    private String bizType;
    private List<String> imageUrls;
    private List<AiAudioInputDTO> audios;
}

