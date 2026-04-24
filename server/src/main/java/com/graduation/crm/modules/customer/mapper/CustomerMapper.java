package com.graduation.crm.modules.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.customer.vo.CustomerDetailVO;
import com.graduation.crm.modules.customer.vo.CustomerListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    IPage<CustomerListVO> selectCustomerPage(IPage<CustomerListVO> page, @Param("query") CustomerQueryDTO query);

    CustomerDetailVO selectCustomerDetail(@Param("id") Long id);
}

