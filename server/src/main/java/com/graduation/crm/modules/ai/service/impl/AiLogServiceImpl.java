package com.graduation.crm.modules.ai.service.impl;

import com.graduation.crm.modules.ai.config.AiProperties;
import com.graduation.crm.modules.ai.entity.AiCallLog;
import com.graduation.crm.modules.ai.mapper.AiCallLogMapper;
import com.graduation.crm.modules.ai.service.AiLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiLogServiceImpl implements AiLogService {

    private final AiProperties aiProperties;
    private final AiCallLogMapper aiCallLogMapper;

    @Override
    public void saveSuccess(String bizType, Long bizId, String modelName, String requestJson, String responseJson, Integer tokenUsage) {
        if (!Boolean.TRUE.equals(aiProperties.getSaveCallLog())) {
            return;
        }
        AiCallLog log = new AiCallLog();
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setModelName(modelName);
        log.setRequestJson(requestJson);
        log.setResponseJson(responseJson);
        log.setTokenUsage(tokenUsage);
        log.setStatus("SUCCESS");
        aiCallLogMapper.insert(log);
    }

    @Override
    public void saveFailed(String bizType, Long bizId, String modelName, String requestJson, String errorMsg) {
        if (!Boolean.TRUE.equals(aiProperties.getSaveCallLog())) {
            return;
        }
        AiCallLog log = new AiCallLog();
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setModelName(modelName);
        log.setRequestJson(requestJson);
        log.setStatus("FAILED");
        log.setErrorMsg(errorMsg);
        aiCallLogMapper.insert(log);
    }
}

