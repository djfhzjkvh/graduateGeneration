package com.graduation.crm.modules.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.report.entity.ReportDailyStat;
import org.apache.ibatis.annotations.Mapper;

/**
 * 每日统计报表 Mapper，供定时任务生成和管理端报表查询复用。
 */
@Mapper
public interface ReportDailyStatMapper extends BaseMapper<ReportDailyStat> {
}
