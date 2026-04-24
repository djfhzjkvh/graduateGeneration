package com.graduation.crm.modules.heat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.heat.entity.CustomerHeatLog;
import com.graduation.crm.modules.heat.vo.HeatDetailVO;
import com.graduation.crm.modules.heat.vo.HighIntentCustomerVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface HeatMapper extends BaseMapper<CustomerHeatLog> {

    HeatDetailVO selectLatestHeat(@Param("customerId") Long customerId);

    int upsertHeatLog(CustomerHeatLog log);

    List<HighIntentCustomerVO> selectHighIntentCustomers(@Param("advisorId") Long advisorId, @Param("managerId") Long managerId);

    Integer countRecentFollows(@Param("customerId") Long customerId, @Param("days") Integer days);

    Integer countFollowKeywordHits(@Param("customerId") Long customerId);

    Integer countVisitOrDealFollows(@Param("customerId") Long customerId);

    BigDecimal avgSentimentScore(@Param("customerId") Long customerId);
}
