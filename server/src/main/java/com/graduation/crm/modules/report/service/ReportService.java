package com.graduation.crm.modules.report.service;

import com.graduation.crm.modules.report.dto.ReportQueryDTO;
import com.graduation.crm.modules.report.vo.ConversionReportVO;
import com.graduation.crm.modules.report.vo.DailyReportVO;
import com.graduation.crm.modules.report.vo.SourceReportVO;

import java.util.List;

public interface ReportService {

    List<DailyReportVO> daily(ReportQueryDTO queryDTO);

    ConversionReportVO conversion(ReportQueryDTO queryDTO);

    List<SourceReportVO> source(ReportQueryDTO queryDTO);
}

