package com.graduation.crm.modules.admin.service;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.admin.dto.AiConfigUpdateDTO;
import com.graduation.crm.modules.admin.dto.AiLogQueryDTO;
import com.graduation.crm.modules.admin.dto.ConfigUpdateDTO;
import com.graduation.crm.modules.admin.dto.OperLogQueryDTO;
import com.graduation.crm.modules.admin.vo.AiCallLogVO;
import com.graduation.crm.modules.admin.vo.AiConfigVO;
import com.graduation.crm.modules.admin.vo.ConfigVO;
import com.graduation.crm.modules.admin.vo.OperLogVO;

import java.util.List;

public interface AdminConfigService {

    List<ConfigVO> listConfigs(String configKey);

    void updateConfig(String key, ConfigUpdateDTO dto);

    AiConfigVO getAiConfig();

    void updateAiConfig(AiConfigUpdateDTO dto);

    PageResult<AiCallLogVO> pageAiLogs(AiLogQueryDTO queryDTO);

    PageResult<OperLogVO> pageOperLogs(OperLogQueryDTO queryDTO);
}

