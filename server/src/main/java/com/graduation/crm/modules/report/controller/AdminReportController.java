package com.graduation.crm.modules.report.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.report.dto.ReportQueryDTO;
import com.graduation.crm.modules.report.service.ReportService;
import com.graduation.crm.modules.report.vo.ConversionReportVO;
import com.graduation.crm.modules.report.vo.DailyReportVO;
import com.graduation.crm.modules.report.vo.SourceReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端报表接口。
 *
 * 面向后台经营分析页面，提供日报、转化率和客户来源三类核心报表。
 */
@Tag(name = "管理端报表接口")
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;

    /**
     * 查询销售日报趋势，可按日期范围和部门过滤。
     */
    @GetMapping("/daily")
    @Operation(summary = "销售日报")
    public Result<List<DailyReportVO>> daily(ReportQueryDTO queryDTO) {
        return Result.success(reportService.daily(queryDTO));
    }

    /**
     * 查询客户转化分析，包括到访率、成交率和流失率。
     */
    @GetMapping("/conversion")
    @Operation(summary = "转化率分析")
    public Result<ConversionReportVO> conversion(ReportQueryDTO queryDTO) {
        return Result.success(reportService.conversion(queryDTO));
    }

    /**
     * 查询客户来源分析，用于判断微信、电话、到访、转介绍等渠道质量。
     */
    @GetMapping("/source")
    @Operation(summary = "客户来源分析")
    public Result<List<SourceReportVO>> source(ReportQueryDTO queryDTO) {
        return Result.success(reportService.source(queryDTO));
    }
}

