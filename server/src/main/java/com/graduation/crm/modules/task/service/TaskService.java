package com.graduation.crm.modules.task.service;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.task.dto.TaskCreateDTO;
import com.graduation.crm.modules.task.dto.TaskDelayDTO;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.dto.TaskRemindLogQueryDTO;
import com.graduation.crm.modules.task.dto.TaskTransferDTO;
import com.graduation.crm.modules.task.vo.TaskRemindLogVO;
import com.graduation.crm.modules.task.vo.TaskStatVO;
import com.graduation.crm.modules.task.vo.TaskVO;

public interface TaskService {

    PageResult<TaskVO> page(TaskQueryDTO queryDTO);

    void create(TaskCreateDTO dto);

    void complete(Long id);

    void delay(Long id, TaskDelayDTO dto);

    void transfer(Long id, TaskTransferDTO dto);

    TaskStatVO stat();

    PageResult<TaskRemindLogVO> remindLogPage(TaskRemindLogQueryDTO queryDTO);

    PageResult<TaskRemindLogVO> remindLogsByTask(Long taskId, TaskRemindLogQueryDTO queryDTO);
}
