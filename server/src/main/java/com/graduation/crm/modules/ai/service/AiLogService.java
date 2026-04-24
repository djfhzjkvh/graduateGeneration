package com.graduation.crm.modules.ai.service;

public interface AiLogService {

    void saveSuccess(String bizType, Long bizId, String modelName, String requestJson, String responseJson, Integer tokenUsage);

    void saveFailed(String bizType, Long bizId, String modelName, String requestJson, String errorMsg);
}

