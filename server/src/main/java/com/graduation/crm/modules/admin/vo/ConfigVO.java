package com.graduation.crm.modules.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfigVO {

    private Long id;
    private String configKey;
    private String configValue;
    private String configName;
    private String remark;
    private LocalDateTime updatedAt;
}

