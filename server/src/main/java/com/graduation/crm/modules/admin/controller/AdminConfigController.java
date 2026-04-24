package com.graduation.crm.modules.admin.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.admin.dto.AiConfigUpdateDTO;
import com.graduation.crm.modules.admin.dto.ConfigUpdateDTO;
import com.graduation.crm.modules.admin.service.AdminConfigService;
import com.graduation.crm.modules.admin.vo.AiConfigVO;
import com.graduation.crm.modules.admin.vo.ConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端系统配置接口。
 *
 * 用于维护跟进节奏、高意向阈值、Prompt版本和 AI 模型配置等系统参数。
 */
@Tag(name = "管理端配置接口")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminConfigController {

    private final AdminConfigService adminConfigService;

    /**
     * 查询系统配置列表，可按配置 key 模糊筛选。
     */
    @GetMapping("/configs")
    @Operation(summary = "查询系统配置")
    public Result<List<ConfigVO>> listConfigs(@RequestParam(required = false) String configKey) {
        return Result.success(adminConfigService.listConfigs(configKey));
    }

    /**
     * 新增或更新指定 key 的系统配置。
     */
    @PutMapping("/configs/{key}")
    @Operation(summary = "更新系统配置")
    public Result<Void> updateConfig(@PathVariable String key, @RequestBody @Valid ConfigUpdateDTO dto) {
        adminConfigService.updateConfig(key, dto);
        return Result.success();
    }

    /**
     * 查询当前启用的 AI 模型配置，接口不会返回真实 API Key。
     */
    @GetMapping("/ai/config")
    @Operation(summary = "查询AI配置")
    public Result<AiConfigVO> getAiConfig() {
        return Result.success(adminConfigService.getAiConfig());
    }

    /**
     * 更新 AI 模型配置；apiKey 为空时保留旧密钥。
     */
    @PutMapping("/ai/config")
    @Operation(summary = "更新AI配置")
    public Result<Void> updateAiConfig(@RequestBody @Valid AiConfigUpdateDTO dto) {
        adminConfigService.updateAiConfig(dto);
        return Result.success();
    }
}

