package com.graduation.crm.modules.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.customer.entity.CustomerTag;
import com.graduation.crm.modules.customer.vo.CustomerTagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerTagMapper extends BaseMapper<CustomerTag> {

    List<CustomerTagVO> selectEnabledTags();

    List<String> selectTagNamesByCustomerId(@Param("customerId") Long customerId);
}

