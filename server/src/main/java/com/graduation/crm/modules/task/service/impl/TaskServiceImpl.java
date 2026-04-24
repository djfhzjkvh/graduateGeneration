package com.graduation.crm.modules.task.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.task.dto.TaskCreateDTO;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.entity.Task;
import com.graduation.crm.modules.task.mapper.TaskMapper;
import com.graduation.crm.modules.task.service.TaskService;
import com.graduation.crm.modules.task.vo.TaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    @Override
    public PageResult<TaskVO> page(TaskQueryDTO queryDTO) {
        // 任务列表需要关联客户和负责人姓名，查询逻辑放在 XML 中统一维护。
        Page<TaskVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<TaskVO> result = taskMapper.selectTaskPage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public void create(TaskCreateDTO dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        // 日期和时间拆字段存储，便于日历视图按天聚合任务。
        task.setTaskDate(LocalDate.parse(dto.getTaskDate()));
        if (dto.getTaskTime() != null && !"".equals(dto.getTaskTime().trim())) {
            task.setTaskTime(LocalTime.parse(dto.getTaskTime()));
        }
        // 新建任务默认待完成，中优先级；延期次数由延期接口单独维护。
        task.setStatus("PENDING");
        task.setPriority(dto.getPriority() == null ? "MEDIUM" : dto.getPriority());
        task.setDelayCount(0);
        task.setDeleted(0);
        taskMapper.insert(task);
    }

    @Override
    public void complete(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null || Integer.valueOf(1).equals(task.getDeleted())) {
            throw new BusinessException("任务不存在");
        }
        // 完成任务只更新状态和完成时间，任务内容保留作为跟进过程证据。
        task.setStatus("DONE");
        task.setCompleteTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }
}
