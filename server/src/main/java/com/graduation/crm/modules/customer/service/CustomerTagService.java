package com.graduation.crm.modules.customer.service;

import com.graduation.crm.modules.customer.dto.CustomerTagBindDTO;
import com.graduation.crm.modules.customer.dto.CustomerTagCreateDTO;
import com.graduation.crm.modules.customer.dto.CustomerTagUpdateDTO;
import com.graduation.crm.modules.customer.vo.CustomerTagVO;

import java.util.List;

public interface CustomerTagService {

    List<CustomerTagVO> listEnabled();

    void create(CustomerTagCreateDTO dto);

    void update(Long id, CustomerTagUpdateDTO dto);

    void delete(Long id);

    void bindCustomerTags(Long customerId, CustomerTagBindDTO dto);
}

