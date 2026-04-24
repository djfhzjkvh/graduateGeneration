package com.graduation.crm.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_model_config")
public class AiModelConfig {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String providerName;
    private String modelName;
    private String apiUrl;
    private String apiKey;
    private BigDecimal temperature;
    private Integer status;
    private LocalDateTime updatedAt;
}

