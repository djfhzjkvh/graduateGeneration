package com.graduation.crm.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.admin.dto.AiConfigUpdateDTO;
import com.graduation.crm.modules.admin.dto.AiLogQueryDTO;
import com.graduation.crm.modules.admin.dto.ConfigUpdateDTO;
import com.graduation.crm.modules.admin.dto.OperLogQueryDTO;
import com.graduation.crm.modules.admin.entity.AiModelConfig;
import com.graduation.crm.modules.admin.entity.SysConfig;
import com.graduation.crm.modules.admin.mapper.AdminAiConfigMapper;
import com.graduation.crm.modules.admin.mapper.AdminConfigMapper;
import com.graduation.crm.modules.admin.mapper.AdminLogMapper;
import com.graduation.crm.modules.admin.service.AdminConfigService;
import com.graduation.crm.modules.admin.vo.AiCallLogVO;
import com.graduation.crm.modules.admin.vo.AiConfigVO;
import com.graduation.crm.modules.admin.vo.ConfigVO;
import com.graduation.crm.modules.admin.vo.OperLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminConfigServiceImpl implements AdminConfigService {

    private final AdminConfigMapper adminConfigMapper;
    private final AdminAiConfigMapper adminAiConfigMapper;
    private final AdminLogMapper adminLogMapper;

    @Override
    public List<ConfigVO> listConfigs(String configKey) {
        List<SysConfig> configs = adminConfigMapper.selectList(new LambdaQueryWrapper<SysConfig>()
                .like(configKey != null && !"".equals(configKey.trim()), SysConfig::getConfigKey, configKey)
                .orderByAsc(SysConfig::getConfigKey));
        return configs.stream().map(item -> {
            ConfigVO vo = new ConfigVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateConfig(String key, ConfigUpdateDTO dto) {
        SysConfig config = adminConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key));
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(key);
        }
        config.setConfigValue(dto.getConfigValue());
        config.setConfigName(dto.getConfigName() == null ? key : dto.getConfigName());
        config.setRemark(dto.getRemark());
        if (config.getId() == null) {
            adminConfigMapper.insert(config);
        } else {
            adminConfigMapper.updateById(config);
        }
    }

    @Override
    public AiConfigVO getAiConfig() {
        AiModelConfig config = adminAiConfigMapper.selectOne(new LambdaQueryWrapper<AiModelConfig>()
                .eq(AiModelConfig::getStatus, 1)
                .last("LIMIT 1"));
        if (config == null) {
            throw new BusinessException("AI模型配置不存在");
        }
        return toAiConfigVO(config);
    }

    @Override
    public void updateAiConfig(AiConfigUpdateDTO dto) {
        AiModelConfig config = adminAiConfigMapper.selectOne(new LambdaQueryWrapper<AiModelConfig>()
                .eq(AiModelConfig::getStatus, 1)
                .last("LIMIT 1"));
        if (config == null) {
            config = new AiModelConfig();
        }
        config.setProviderName(dto.getProviderName());
        config.setModelName(dto.getModelName());
        config.setApiUrl(dto.getApiUrl());
        if (dto.getApiKey() != null && !"".equals(dto.getApiKey().trim())) {
            config.setApiKey(dto.getApiKey());
        }
        config.setTemperature(dto.getTemperature());
        config.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        if (config.getId() == null) {
            adminAiConfigMapper.insert(config);
        } else {
            adminAiConfigMapper.updateById(config);
        }
    }

    @Override
    public PageResult<AiCallLogVO> pageAiLogs(AiLogQueryDTO queryDTO) {
        Page<AiCallLogVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<AiCallLogVO> result = adminLogMapper.selectAiLogPage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<OperLogVO> pageOperLogs(OperLogQueryDTO queryDTO) {
        Page<OperLogVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<OperLogVO> result = adminLogMapper.selectOperLogPage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    private AiConfigVO toAiConfigVO(AiModelConfig config) {
        AiConfigVO vo = new AiConfigVO();
        BeanUtils.copyProperties(config, vo);
        // 管理端只展示密钥是否配置，避免把真实 API Key 回传到前端。
        vo.setApiKeyConfigured(config.getApiKey() != null && !"".equals(config.getApiKey().trim()));
        return vo;
    }
}

