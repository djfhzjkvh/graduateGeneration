package com.graduation.crm.modules.message.service;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.message.dto.MessageQueryDTO;
import com.graduation.crm.modules.message.vo.MessageVO;

public interface MessageService {

    PageResult<MessageVO> page(MessageQueryDTO queryDTO);

    void read(Long id);

    void readAll(Long userId);
}

