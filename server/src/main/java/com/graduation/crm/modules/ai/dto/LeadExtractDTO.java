package com.graduation.crm.modules.ai.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class LeadExtractDTO {

    @NotBlank(message = "来源类型不能为空")
    private String sourceType;

    private Long sourceFileId;
    private String rawText;
    private List<String> imageUrls;
    private List<AiAudioInputDTO> audios;
    private Long createdBy;
}

