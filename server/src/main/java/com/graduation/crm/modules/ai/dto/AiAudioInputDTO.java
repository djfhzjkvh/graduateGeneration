package com.graduation.crm.modules.ai.dto;

import lombok.Data;

@Data
public class AiAudioInputDTO {

    /**
     * Base64 音频内容。当前按 OpenAI-compatible input_audio 结构发送。
     */
    private String data;
    private String format;
}

