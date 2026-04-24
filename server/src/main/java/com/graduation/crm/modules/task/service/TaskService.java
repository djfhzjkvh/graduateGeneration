package com.graduation.crm.modules.task.service;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.task.dto.TaskCreateDTO;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.vo.TaskVO;

public interface TaskService {

    PageResult<TaskVO> page(TaskQueryDTO queryDTO);

    void create(TaskCreateDTO dto);

    void complete(Long id);
}

