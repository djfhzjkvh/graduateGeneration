package com.graduation.crm.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 轻量接口权限配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 默认关闭，避免影响开发联调；答辩演示权限控制时可改为 true。
     */
    private Boolean enabled = false;
}
