package com.graduation.crm.modules.admin.service;

public interface SystemConfigService {

    String getConfigValue(String key, String defaultValue);

    Integer getIntConfig(String key, Integer defaultValue);

    Integer getHighIntentThreshold();
}
