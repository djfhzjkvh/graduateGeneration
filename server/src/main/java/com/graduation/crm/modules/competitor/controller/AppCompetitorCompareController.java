package com.graduation.crm.modules.competitor.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.competitor.dto.CompetitorCompareDTO;
import com.graduation.crm.modules.competitor.service.CompetitorCompareService;
import com.graduation.crm.modules.competitor.vo.CompetitorAiResponseVO;
import com.graduation.crm.modules.competitor.vo.CompetitorCompareVO;
import com.graduation.crm.modules.competitor.vo.CustomerCompetitorFocusVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 小程序端竞品对比接口。
 *
 * 用于顾问在客户提到竞品时快速生成对比结论和一句话应对话术。
 */
@Tag(name = "移动端竞品对比接口")
@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppCompetitorCompareController {

    private final CompetitorCompareService competitorCompareService;

    /**
     * 基于客户、本楼盘和竞品资料生成结构化对比结果，不调用 AI。
     */
    @PostMapping("/competitors/compare")
    @Operation(summary = "竞品资料对比")
    public Result<CompetitorCompareVO> compare(@RequestBody @Valid CompetitorCompareDTO dto) {
        return Result.success(competitorCompareService.compare(dto));
    }

    /**
     * 调用 Qwen 生成一句话竞品应对模板，并保存客户关注竞品记录。
     */
    @PostMapping("/competitors/ai-response")
    @Operation(summary = "生成竞品应对话术")
    public Result<CompetitorAiResponseVO> aiResponse(@RequestBody @Valid CompetitorCompareDTO dto) {
        return Result.success(competitorCompareService.aiResponse(dto));
    }

    /**
     * 查询客户历史关注竞品记录，用于客户详情页持续追踪议价关注点。
     */
    @GetMapping("/customers/{customerId}/competitor-history")
    @Operation(summary = "查询客户竞品关注历史")
    public Result<List<CustomerCompetitorFocusVO>> history(@PathVariable Long customerId) {
        return Result.success(competitorCompareService.history(customerId));
    }
}

