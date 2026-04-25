package com.graduation.crm.modules.prediction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.customer.mapper.CustomerMapper;
import com.graduation.crm.modules.prediction.dto.PredictionQueryDTO;
import com.graduation.crm.modules.prediction.service.PredictionService;
import com.graduation.crm.modules.prediction.vo.PredictionOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionServiceImpl implements PredictionService {

    private final CustomerMapper customerMapper;

    @Override
    public PredictionOverviewVO overview(PredictionQueryDTO queryDTO) {
        List<Customer> customers = customerMapper.selectList(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getDeleted, 0));
        int count = customers.size();
        int expectedDeal = (int) customers.stream()
                .filter(customer -> customer.getHeatScore() != null && customer.getHeatScore() >= 80)
                .count();
        BigDecimal amount = customers.stream()
                .filter(customer -> customer.getHeatScore() != null && customer.getHeatScore() >= 80)
                .map(customer -> customer.getBudgetMax() == null ? BigDecimal.ZERO : customer.getBudgetMax())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgProbability = count == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(
                customers.stream().mapToInt(customer -> customer.getHeatScore() == null ? 0 : customer.getHeatScore()).average().orElse(0) / 100D
        ).setScale(4, RoundingMode.HALF_UP);

        PredictionOverviewVO vo = new PredictionOverviewVO();
        vo.setCustomerCount(count);
        vo.setExpectedDealCount(expectedDeal);
        vo.setExpectedAmount(amount);
        vo.setAvgProbability(avgProbability);
        vo.setHighProbabilityAmount(amount);
        return vo;
    }
}
