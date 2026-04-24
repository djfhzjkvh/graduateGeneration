package com.graduation.crm.modules.customer.service;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.customer.dto.CustomerCreateDTO;
import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerUpdateDTO;
import com.graduation.crm.modules.customer.vo.CustomerDetailVO;
import com.graduation.crm.modules.customer.vo.CustomerListVO;

public interface CustomerService {

    PageResult<CustomerListVO> page(CustomerQueryDTO queryDTO);

    CustomerDetailVO detail(Long id);

    void create(CustomerCreateDTO dto);

    void update(Long id, CustomerUpdateDTO dto);

    void delete(Long id);
}

