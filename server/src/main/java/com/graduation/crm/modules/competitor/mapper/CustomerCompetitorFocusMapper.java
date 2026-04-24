package com.graduation.crm.modules.competitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.competitor.entity.CustomerCompetitorFocus;
import com.graduation.crm.modules.competitor.vo.CustomerCompetitorFocusVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerCompetitorFocusMapper extends BaseMapper<CustomerCompetitorFocus> {

    List<CustomerCompetitorFocusVO> selectByCustomerId(@Param("customerId") Long customerId);
}

