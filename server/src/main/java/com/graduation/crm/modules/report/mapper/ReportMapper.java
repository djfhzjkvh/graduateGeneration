package com.graduation.crm.modules.report.mapper;

import com.graduation.crm.modules.report.dto.ReportQueryDTO;
import com.graduation.crm.modules.report.vo.DailyReportVO;
import com.graduation.crm.modules.report.vo.SourceReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper {

    List<DailyReportVO> selectDailyReports(@Param("query") ReportQueryDTO query);

    Integer countCustomers(@Param("query") ReportQueryDTO query, @Param("status") String status);

    List<SourceReportVO> selectSourceReports(@Param("query") ReportQueryDTO query);
}

