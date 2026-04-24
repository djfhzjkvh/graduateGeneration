package com.graduation.crm.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.customer.dto.CustomerTagBindDTO;
import com.graduation.crm.modules.customer.dto.CustomerTagCreateDTO;
import com.graduation.crm.modules.customer.dto.CustomerTagUpdateDTO;
import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.customer.entity.CustomerTag;
import com.graduation.crm.modules.customer.entity.CustomerTagRel;
import com.graduation.crm.modules.customer.mapper.CustomerMapper;
import com.graduation.crm.modules.customer.mapper.CustomerTagMapper;
import com.graduation.crm.modules.customer.mapper.CustomerTagRelMapper;
import com.graduation.crm.modules.customer.service.CustomerTagService;
import com.graduation.crm.modules.customer.vo.CustomerTagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerTagServiceImpl implements CustomerTagService {

    private final CustomerMapper customerMapper;
    private final CustomerTagMapper customerTagMapper;
    private final CustomerTagRelMapper customerTagRelMapper;

    @Override
    public List<CustomerTagVO> listEnabled() {
        return customerTagMapper.selectEnabledTags();
    }

    @Override
    public void create(CustomerTagCreateDTO dto) {
        CustomerTag exists = customerTagMapper.selectOne(new LambdaQueryWrapper<CustomerTag>()
                .eq(CustomerTag::getTagName, dto.getTagName()));
        if (exists != null) {
            throw new BusinessException("标签名称已存在");
        }
        CustomerTag tag = new CustomerTag();
        BeanUtils.copyProperties(dto, tag);
        tag.setTagType(dto.getTagType() == null ? "CUSTOM" : dto.getTagType());
        tag.setStatus(1);
        customerTagMapper.insert(tag);
    }

    @Override
    public void update(Long id, CustomerTagUpdateDTO dto) {
        CustomerTag tag = customerTagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        BeanUtils.copyProperties(dto, tag);
        customerTagMapper.updateById(tag);
    }

    @Override
    public void delete(Long id) {
        CustomerTag tag = customerTagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        // 标签删除采用停用方式，保留客户历史标签关系，避免详情页历史数据断裂。
        tag.setStatus(0);
        customerTagMapper.updateById(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindCustomerTags(Long customerId, CustomerTagBindDTO dto) {
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null || Integer.valueOf(1).equals(customer.getDeleted())) {
            throw new BusinessException("客户不存在");
        }

        customerTagRelMapper.delete(new LambdaQueryWrapper<CustomerTagRel>()
                .eq(CustomerTagRel::getCustomerId, customerId));

        List<Long> tagIds = dto.getTagIds() == null ? Collections.emptyList() : dto.getTagIds();
        // 绑定标签采用“先删后插”的全量覆盖，前端只需提交当前选中的标签集合。
        for (Long tagId : tagIds) {
            CustomerTagRel rel = new CustomerTagRel();
            rel.setCustomerId(customerId);
            rel.setTagId(tagId);
            customerTagRelMapper.insert(rel);
        }
    }
}

