package com.graduation.crm.modules.admin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduation.crm.modules.admin.dto.AiLogQueryDTO;
import com.graduation.crm.modules.admin.dto.OperLogQueryDTO;
import com.graduation.crm.modules.admin.entity.SysOperLog;
import com.graduation.crm.modules.admin.vo.AiCallLogVO;
import com.graduation.crm.modules.admin.vo.OperLogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminLogMapper {

    IPage<AiCallLogVO> selectAiLogPage(IPage<AiCallLogVO> page, @Param("query") AiLogQueryDTO query);

    IPage<OperLogVO> selectOperLogPage(IPage<OperLogVO> page, @Param("query") OperLogQueryDTO query);
}

