package com.graduation.crm.modules.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.task.entity.TaskTransferLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务转派日志 Mapper。
 */
@Mapper
public interface TaskTransferLogMapper extends BaseMapper<TaskTransferLog> {
}
