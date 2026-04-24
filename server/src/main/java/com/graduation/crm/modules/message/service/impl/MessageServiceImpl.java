package com.graduation.crm.modules.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.message.dto.MessageQueryDTO;
import com.graduation.crm.modules.message.entity.SysMessage;
import com.graduation.crm.modules.message.mapper.MessageMapper;
import com.graduation.crm.modules.message.service.MessageService;
import com.graduation.crm.modules.message.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

    @Override
    public PageResult<MessageVO> page(MessageQueryDTO queryDTO) {
        // 消息中心按用户隔离，前端可按已读/未读状态过滤。
        Page<MessageVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<MessageVO> result = messageMapper.selectMessagePage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public void read(Long id) {
        SysMessage message = messageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        message.setReadStatus(1);
        messageMapper.updateById(message);
    }

    @Override
    public void readAll(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        // 全部已读只影响当前用户，避免误改其他人的消息状态。
        messageMapper.update(null, new LambdaUpdateWrapper<SysMessage>()
                .eq(SysMessage::getUserId, userId)
                .eq(SysMessage::getReadStatus, 0)
                .set(SysMessage::getReadStatus, 1));
    }
}

