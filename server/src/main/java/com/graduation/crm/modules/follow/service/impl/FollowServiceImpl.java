package com.graduation.crm.modules.follow.service.impl;

import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.customer.mapper.CustomerMapper;
import com.graduation.crm.modules.follow.dto.FollowCreateDTO;
import com.graduation.crm.modules.follow.entity.FollowRecord;
import com.graduation.crm.modules.follow.mapper.FollowRecordMapper;
import com.graduation.crm.modules.follow.service.FollowService;
import com.graduation.crm.modules.follow.vo.FollowRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    /**
     * 前端统一传 yyyy-MM-dd HH:mm:ss，避免不同时区和浏览器格式差异。
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final FollowRecordMapper followRecordMapper;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(FollowCreateDTO dto) {
        // 新增跟进时同时更新客户最近跟进时间、下次跟进时间和客户状态。
        Customer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null || Integer.valueOf(1).equals(customer.getDeleted())) {
            throw new BusinessException("客户不存在");
        }

        FollowRecord record = new FollowRecord();
        BeanUtils.copyProperties(dto, record);
        // 登录鉴权接入前允许前端传 userId；未传时默认使用客户负责人。
        record.setUserId(dto.getUserId() == null ? customer.getAdvisorId() : dto.getUserId());
        if (dto.getNextFollowTime() != null && !"".equals(dto.getNextFollowTime().trim())) {
            record.setNextFollowTime(LocalDateTime.parse(dto.getNextFollowTime(), DATE_TIME_FORMATTER));
        }
        followRecordMapper.insert(record);

        customer.setLatestFollowTime(LocalDateTime.now());
        customer.setNextFollowTime(record.getNextFollowTime());
        // 跟进结果会推动客户阶段变化，保持客户列表状态与最新业务动作一致。
        if ("VISITED".equals(dto.getFollowResult())) {
            customer.setStatus("VISITED");
        } else if ("DEAL".equals(dto.getFollowResult())) {
            customer.setStatus("DEAL");
        } else if ("LOST".equals(dto.getFollowResult())) {
            customer.setStatus("LOST");
        } else if ("NEW".equals(customer.getStatus())) {
            customer.setStatus("FOLLOWING");
        }
        customerMapper.updateById(customer);
    }

    @Override
    public List<FollowRecordVO> listByCustomerId(Long customerId) {
        return followRecordMapper.selectByCustomerId(customerId);
    }
}
