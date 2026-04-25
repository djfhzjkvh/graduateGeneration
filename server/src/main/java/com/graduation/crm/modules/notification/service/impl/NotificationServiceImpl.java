package com.graduation.crm.modules.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.crm.modules.message.entity.SysMessage;
import com.graduation.crm.modules.message.mapper.MessageMapper;
import com.graduation.crm.modules.notification.dto.NotificationSendDTO;
import com.graduation.crm.modules.notification.service.NotificationService;
import com.graduation.crm.modules.system.entity.SysUser;
import com.graduation.crm.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final MessageMapper messageMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public Long sendToUser(NotificationSendDTO dto) {
        if (dto == null || dto.getReceiverUserId() == null) {
            return null;
        }
        SysMessage message = new SysMessage();
        message.setUserId(dto.getReceiverUserId());
        message.setMsgType(defaultString(dto.getMsgType(), "SYSTEM"));
        message.setTitle(defaultString(dto.getTitle(), "系统通知"));
        message.setContent(defaultString(dto.getContent(), ""));
        message.setBizType(dto.getBizType());
        message.setBizId(dto.getBizId());
        message.setReadStatus(0);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);
        return message.getId();
    }

    @Override
    public Long sendToChannel(String channelCode, NotificationSendDTO dto) {
        log.info("mock notification channel send: channelCode={}, title={}", channelCode, dto == null ? null : dto.getTitle());
        return 0L;
    }

    @Override
    public int sendToManagers(String title, String content, String bizType, Long bizId) {
        List<SysUser> managers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleted, 0)
                .eq(SysUser::getStatus, 1)
                .in(SysUser::getRoleId, 2L, 3L));
        for (SysUser manager : managers) {
            NotificationSendDTO dto = new NotificationSendDTO();
            dto.setReceiverUserId(manager.getId());
            dto.setMsgType("SYSTEM");
            dto.setTitle(title);
            dto.setContent(content);
            dto.setBizType(bizType);
            dto.setBizId(bizId);
            sendToUser(dto);
        }
        return managers.size();
    }

    @Override
    public int retryFailed(Integer maxRetryCount, Integer batchLimit) {
        log.info("mock retry failed notifications: maxRetryCount={}, batchLimit={}", maxRetryCount, batchLimit);
        return 0;
    }

    private String defaultString(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }
}
