package com.graduation.crm.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduation.crm.modules.message.dto.MessageQueryDTO;
import com.graduation.crm.modules.message.entity.SysMessage;
import com.graduation.crm.modules.message.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper extends BaseMapper<SysMessage> {

    IPage<MessageVO> selectMessagePage(IPage<MessageVO> page, @Param("query") MessageQueryDTO query);
}

