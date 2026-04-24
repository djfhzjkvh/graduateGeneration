package com.graduation.crm.modules.heat.service;

import com.graduation.crm.modules.heat.vo.HeatDetailVO;
import com.graduation.crm.modules.heat.vo.HighIntentCustomerVO;

import java.util.List;

public interface HeatService {

    HeatDetailVO detail(Long customerId);

    HeatDetailVO calculate(Long customerId);

    Integer batchCalculate();

    List<HighIntentCustomerVO> highIntent(Long advisorId, Long managerId);
}

