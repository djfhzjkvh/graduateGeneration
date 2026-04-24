package com.graduation.crm.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.crm.modules.admin.entity.SysConfig;
import com.graduation.crm.modules.admin.mapper.AdminConfigMapper;
import com.graduation.crm.modules.admin.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private static final String HIGH_INTENT_THRESHOLD_KEY = "heat.high.threshold";
    private static final Integer DEFAULT_HIGH_INTENT_THRESHOLD = 80;

    private final AdminConfigMapper adminConfigMapper;

    @Override
    public String getConfigValue(String key, String defaultValue) {
        SysConfig config = adminConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key)
                .last("LIMIT 1"));
        if (config == null || config.getConfigValue() == null || config.getConfigValue().trim().isEmpty()) {
            return defaultValue;
        }
        return config.getConfigValue().trim();
    }

    @Override
    public Integer getIntConfig(String key, Integer defaultValue) {
        String value = getConfigValue(key, String.valueOf(defaultValue));
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            log.warn("Invalid integer system config, key={}, value={}, fallback={}", key, value, defaultValue);
            return defaultValue;
        }
    }

    @Override
    public Integer getHighIntentThreshold() {
        return getIntConfig(HIGH_INTENT_THRESHOLD_KEY, DEFAULT_HIGH_INTENT_THRESHOLD);
    }
}
