package com.graduation.crm.modules.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.admin.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志写入 Mapper。
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
