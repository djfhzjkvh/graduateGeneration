package com.graduation.crm.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.task.entity.TaskRemindLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务提醒日志 Mapper，用于即将到期提醒去重和后续提醒记录追踪。
 */
@Mapper
public interface TaskRemindLogMapper extends BaseMapper<TaskRemindLog> {
}
