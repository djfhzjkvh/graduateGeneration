package com.graduation.crm.modules.heat.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.heat.service.HeatService;
import com.graduation.crm.modules.heat.vo.HeatDetailVO;
import com.graduation.crm.modules.heat.vo.HighIntentCustomerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序端热度评分接口。
 *
 * 为顾问客户详情和高意向客户池提供热度分、原因和下一步建议。
 */
@Tag(name = "移动端热度接口")
@RestController
@RequestMapping("/api/app/heat")
@RequiredArgsConstructor
public class AppHeatController {

    private final HeatService heatService;

    /**
     * 查询客户最新热度，如果没有历史记录会即时计算一次。
     */
    @GetMapping("/{customerId}")
    @Operation(summary = "查询客户热度")
    public Result<HeatDetailVO> detail(@PathVariable Long customerId) {
        return Result.success(heatService.detail(customerId));
    }

    /**
     * 重新计算客户热度，并同步更新客户主表 heat_score。
     */
    @PostMapping("/calculate/{customerId}")
    @Operation(summary = "计算客户热度")
    public Result<HeatDetailVO> calculate(@PathVariable Long customerId) {
        return Result.success(heatService.calculate(customerId));
    }

    /**
     * 查询顾问维度高意向客户池，userId 对应客户 advisor_id。
     */
    @GetMapping("/high-intent")
    @Operation(summary = "查询高意向客户池")
    public Result<List<HighIntentCustomerVO>> highIntent(@RequestParam(required = false) Long advisorId) {
        return Result.success(heatService.highIntent(advisorId, null));
    }
}

