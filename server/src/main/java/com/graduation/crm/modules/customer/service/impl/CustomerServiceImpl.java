package com.graduation.crm.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.customer.dto.CustomerCreateDTO;
import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerUpdateDTO;
import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.customer.mapper.CustomerMapper;
import com.graduation.crm.modules.customer.service.CustomerService;
import com.graduation.crm.modules.customer.vo.CustomerDetailVO;
import com.graduation.crm.modules.customer.vo.CustomerListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    @Override
    public PageResult<CustomerListVO> page(CustomerQueryDTO queryDTO) {
        // 列表查询使用 XML，便于维护多条件筛选和关联展示字段。
        Page<CustomerListVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<CustomerListVO> result = customerMapper.selectCustomerPage(page, queryDTO);
        log.debug("Customer page query finished, total={}, pageNum={}, pageSize={}",
                result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public CustomerDetailVO detail(Long id) {
        CustomerDetailVO detail = customerMapper.selectCustomerDetail(id);
        if (detail == null) {
            throw new BusinessException("客户不存在");
        }
        log.debug("Customer detail query finished, id={}", id);
        return detail;
    }

    @Override
    public void create(CustomerCreateDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        // 新建线索统一从 NEW 状态开始，热度分后续由规则或 AI 模块刷新。
        customer.setStatus("NEW");
        customer.setHeatScore(0);
        customer.setDeleted(0);
        customerMapper.insert(customer);
        log.info("Customer created, id={}", customer.getId());
    }

    @Override
    public void update(Long id, CustomerUpdateDTO dto) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null || Integer.valueOf(1).equals(customer.getDeleted())) {
            throw new BusinessException("客户不存在");
        }
        BeanUtils.copyProperties(dto, customer);
        customerMapper.updateById(customer);
        log.info("Customer updated, id={}", id);
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null || Integer.valueOf(1).equals(customer.getDeleted())) {
            throw new BusinessException("客户不存在");
        }
        // 客户数据涉及跟进和任务链路，默认采用逻辑删除保留历史记录。
        customer.setDeleted(1);
        customerMapper.updateById(customer);
        log.info("Customer logically deleted, id={}", id);
    }
}
