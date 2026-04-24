package com.graduation.crm.modules.report.service.impl;

import com.graduation.crm.modules.report.dto.ReportQueryDTO;
import com.graduation.crm.modules.report.mapper.ReportMapper;
import com.graduation.crm.modules.report.service.ReportService;
import com.graduation.crm.modules.report.vo.ConversionReportVO;
import com.graduation.crm.modules.report.vo.DailyReportVO;
import com.graduation.crm.modules.report.vo.SourceReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;

    @Override
    public List<DailyReportVO> daily(ReportQueryDTO queryDTO) {
        // 日报优先读取 report_daily_stat，适合管理端展示趋势图和日报表格。
        return reportMapper.selectDailyReports(queryDTO);
    }

    @Override
    public ConversionReportVO conversion(ReportQueryDTO queryDTO) {
        Integer total = safe(reportMapper.countCustomers(queryDTO, null));
        Integer visited = safe(reportMapper.countCustomers(queryDTO, "VISITED"));
        Integer deal = safe(reportMapper.countCustomers(queryDTO, "DEAL"));
        Integer lost = safe(reportMapper.countCustomers(queryDTO, "LOST"));

        ConversionReportVO vo = new ConversionReportVO();
        vo.setTotalCustomerCount(total);
        vo.setVisitedCustomerCount(visited);
        vo.setDealCustomerCount(deal);
        vo.setLostCustomerCount(lost);
        vo.setVisitRate(rate(visited, total));
        vo.setDealRate(rate(deal, total));
        vo.setLostRate(rate(lost, total));
        return vo;
    }

    @Override
    public List<SourceReportVO> source(ReportQueryDTO queryDTO) {
        return reportMapper.selectSourceReports(queryDTO);
    }

    private Integer safe(Integer value) {
        return value == null ? 0 : value;
    }

    private BigDecimal rate(Integer numerator, Integer denominator) {
        if (denominator == null || denominator == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
    }
}

