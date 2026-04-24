package com.graduation.crm.modules.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.admin.entity.SysOperLog;
import com.graduation.crm.modules.admin.mapper.SysOperLogMapper;
import com.graduation.crm.modules.task.dto.TaskCreateDTO;
import com.graduation.crm.modules.task.dto.TaskDelayDTO;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.dto.TaskRemindLogQueryDTO;
import com.graduation.crm.modules.task.dto.TaskTransferDTO;
import com.graduation.crm.modules.task.dto.TaskTransferLogQueryDTO;
import com.graduation.crm.modules.task.entity.Task;
import com.graduation.crm.modules.task.entity.TaskRemindLog;
import com.graduation.crm.modules.task.entity.TaskTransferLog;
import com.graduation.crm.modules.task.mapper.TaskMapper;
import com.graduation.crm.modules.task.mapper.TaskRemindLogMapper;
import com.graduation.crm.modules.task.mapper.TaskTransferLogMapper;
import com.graduation.crm.modules.task.service.TaskService;
import com.graduation.crm.modules.task.vo.TaskDetailVO;
import com.graduation.crm.modules.task.vo.TaskRemindLogVO;
import com.graduation.crm.modules.task.vo.TaskStatVO;
import com.graduation.crm.modules.task.vo.TaskTransferLogVO;
import com.graduation.crm.modules.task.vo.TaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务业务服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_DONE = "DONE";
    private static final String STATUS_OVERDUE = "OVERDUE";
    private static final String BIZ_TYPE_TASK = "TASK";

    private final TaskMapper taskMapper;
    private final TaskRemindLogMapper taskRemindLogMapper;
    private final TaskTransferLogMapper taskTransferLogMapper;
    private final SysOperLogMapper sysOperLogMapper;

    @Override
    public PageResult<TaskVO> page(TaskQueryDTO queryDTO) {
        // 任务列表需要关联客户和负责人姓名，查询逻辑放在 XML 中统一维护。
        Page<TaskVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<TaskVO> result = taskMapper.selectTaskPage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public TaskDetailVO detail(Long id) {
        TaskVO task = taskMapper.selectTaskDetail(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        TaskDetailVO detail = new TaskDetailVO();
        detail.setTask(task);
        detail.setRemindLogs(listRemindLogs(id));
        detail.setTransferLogs(listTransferLogs(id));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(TaskCreateDTO dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        // 日期和时间拆字段存储，便于日历视图按天聚合任务。
        task.setTaskDate(LocalDate.parse(dto.getTaskDate()));
        if (dto.getTaskTime() != null && !"".equals(dto.getTaskTime().trim())) {
            task.setTaskTime(LocalTime.parse(dto.getTaskTime()));
        }
        // 新建任务默认待完成，中优先级；延期次数由延期接口单独维护。
        task.setStatus(STATUS_PENDING);
        task.setPriority(dto.getPriority() == null ? "MEDIUM" : dto.getPriority());
        task.setDelayCount(0);
        task.setDeleted(0);
        taskMapper.insert(task);
        saveOperLog(task.getOwnerId(), task.getId(), "TASK_CREATE", "创建任务：" + task.getTitle());
        log.info("任务创建：taskId={}, ownerId={}", task.getId(), task.getOwnerId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        Task task = getAvailableTask(id);
        // 完成任务只更新状态和完成时间，任务内容保留作为跟进过程证据。
        task.setStatus(STATUS_DONE);
        task.setCompleteTime(LocalDateTime.now());
        taskMapper.updateById(task);
        saveOperLog(task.getOwnerId(), id, "TASK_COMPLETE", "完成任务：" + task.getTitle());
        log.info("任务完成：taskId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delay(Long id, TaskDelayDTO dto) {
        Task task = getAvailableTask(id);
        if (STATUS_DONE.equals(task.getStatus())) {
            throw new BusinessException("已完成任务不能延期");
        }

        LocalDate newTaskDate = LocalDate.parse(dto.getNewTaskDate());
        LocalTime newTaskTime = null;
        if (dto.getNewTaskTime() != null && !"".equals(dto.getNewTaskTime().trim())) {
            newTaskTime = LocalTime.parse(dto.getNewTaskTime());
        }

        // 延期后的任务仍保持 PENDING，避免从顾问待办列表中消失；延期次数用于展示和统计。
        task.setTaskDate(newTaskDate);
        task.setTaskTime(newTaskTime);
        task.setStatus(STATUS_PENDING);
        task.setDelayCount(task.getDelayCount() == null ? 1 : task.getDelayCount() + 1);
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);
        saveOperLog(task.getOwnerId(), id, "TASK_DELAY",
                "延期任务到 " + dto.getNewTaskDate() + " " + nullToEmpty(dto.getNewTaskTime()) + "，原因：" + nullToEmpty(dto.getReason()));
        log.info("任务延期：taskId={}, newTaskDate={}, newTaskTime={}, reason={}",
                id, dto.getNewTaskDate(), dto.getNewTaskTime(), dto.getReason());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long id, TaskTransferDTO dto) {
        Task task = getAvailableTask(id);
        if (STATUS_DONE.equals(task.getStatus())) {
            throw new BusinessException("已完成任务不能转派");
        }
        if (task.getOwnerId() != null && task.getOwnerId().equals(dto.getToUserId())) {
            throw new BusinessException("接收人不能与当前负责人相同");
        }

        Long fromUserId = task.getOwnerId();
        task.setOwnerId(dto.getToUserId());
        task.setUpdatedAt(LocalDateTime.now());
        taskMapper.updateById(task);

        // 转派日志用于经理追踪任务责任人变化，后续也可以扩展为转派记录页面。
        TaskTransferLog transferLog = new TaskTransferLog();
        transferLog.setTaskId(id);
        transferLog.setFromUserId(fromUserId);
        transferLog.setToUserId(dto.getToUserId());
        transferLog.setReason(dto.getReason());
        transferLog.setCreatedBy(dto.getCreatedBy());
        transferLog.setCreatedAt(LocalDateTime.now());
        taskTransferLogMapper.insert(transferLog);

        saveOperLog(dto.getCreatedBy(), id, "TASK_TRANSFER",
                "任务转派：fromUserId=" + fromUserId + ", toUserId=" + dto.getToUserId()
                        + "，原因：" + nullToEmpty(dto.getReason()));
        log.info("任务转派：taskId={}, fromUserId={}, toUserId={}, createdBy={}",
                id, fromUserId, dto.getToUserId(), dto.getCreatedBy());
    }

    @Override
    public TaskStatVO stat() {
        TaskStatVO stat = new TaskStatVO();
        stat.setPendingCount(countByStatus(STATUS_PENDING));
        stat.setOverdueCount(countByStatus(STATUS_OVERDUE));
        stat.setDoneCount(countByStatus(STATUS_DONE));
        stat.setDelayedCount(taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                .eq(Task::getDeleted, 0)
                .gt(Task::getDelayCount, 0)));
        return stat;
    }

    @Override
    public PageResult<TaskRemindLogVO> remindLogPage(TaskRemindLogQueryDTO queryDTO) {
        Page<TaskRemindLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<TaskRemindLog> wrapper = buildRemindLogWrapper(queryDTO);
        IPage<TaskRemindLog> result = taskRemindLogMapper.selectPage(page, wrapper);
        List<TaskRemindLogVO> list = result.getRecords().stream()
                .map(this::toRemindLogVO)
                .collect(Collectors.toList());
        return PageResult.of(list, result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<TaskRemindLogVO> remindLogsByTask(Long taskId, TaskRemindLogQueryDTO queryDTO) {
        queryDTO.setTaskId(taskId);
        return remindLogPage(queryDTO);
    }

    @Override
    public PageResult<TaskTransferLogVO> transferLogPage(TaskTransferLogQueryDTO queryDTO) {
        Page<TaskTransferLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<TaskTransferLog> wrapper = buildTransferLogWrapper(queryDTO);
        IPage<TaskTransferLog> result = taskTransferLogMapper.selectPage(page, wrapper);
        List<TaskTransferLogVO> list = result.getRecords().stream()
                .map(this::toTransferLogVO)
                .collect(Collectors.toList());
        return PageResult.of(list, result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<TaskTransferLogVO> transferLogsByTask(Long taskId, TaskTransferLogQueryDTO queryDTO) {
        queryDTO.setTaskId(taskId);
        return transferLogPage(queryDTO);
    }

    private Task getAvailableTask(Long id) {
        Task task = taskMapper.selectById(id);
        if (task == null || Integer.valueOf(1).equals(task.getDeleted())) {
            throw new BusinessException("任务不存在");
        }
        return task;
    }

    private Long countByStatus(String status) {
        return taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                .eq(Task::getDeleted, 0)
                .eq(Task::getStatus, status));
    }

    private List<TaskRemindLogVO> listRemindLogs(Long taskId) {
        List<TaskRemindLog> logs = taskRemindLogMapper.selectList(new LambdaQueryWrapper<TaskRemindLog>()
                .eq(TaskRemindLog::getTaskId, taskId)
                .orderByDesc(TaskRemindLog::getCreatedAt));
        return logs.stream().map(this::toRemindLogVO).collect(Collectors.toList());
    }

    private List<TaskTransferLogVO> listTransferLogs(Long taskId) {
        List<TaskTransferLog> logs = taskTransferLogMapper.selectList(new LambdaQueryWrapper<TaskTransferLog>()
                .eq(TaskTransferLog::getTaskId, taskId)
                .orderByDesc(TaskTransferLog::getCreatedAt));
        return logs.stream().map(this::toTransferLogVO).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<TaskRemindLog> buildRemindLogWrapper(TaskRemindLogQueryDTO queryDTO) {
        LambdaQueryWrapper<TaskRemindLog> wrapper = new LambdaQueryWrapper<TaskRemindLog>()
                .orderByDesc(TaskRemindLog::getCreatedAt);
        if (queryDTO.getTaskId() != null) {
            wrapper.eq(TaskRemindLog::getTaskId, queryDTO.getTaskId());
        }
        if (queryDTO.getRemindType() != null && !"".equals(queryDTO.getRemindType().trim())) {
            wrapper.eq(TaskRemindLog::getRemindType, queryDTO.getRemindType());
        }
        if (queryDTO.getStatus() != null && !"".equals(queryDTO.getStatus().trim())) {
            wrapper.eq(TaskRemindLog::getStatus, queryDTO.getStatus());
        }
        return wrapper;
    }

    private LambdaQueryWrapper<TaskTransferLog> buildTransferLogWrapper(TaskTransferLogQueryDTO queryDTO) {
        LambdaQueryWrapper<TaskTransferLog> wrapper = new LambdaQueryWrapper<TaskTransferLog>()
                .orderByDesc(TaskTransferLog::getCreatedAt);
        if (queryDTO.getTaskId() != null) {
            wrapper.eq(TaskTransferLog::getTaskId, queryDTO.getTaskId());
        }
        if (queryDTO.getFromUserId() != null) {
            wrapper.eq(TaskTransferLog::getFromUserId, queryDTO.getFromUserId());
        }
        if (queryDTO.getToUserId() != null) {
            wrapper.eq(TaskTransferLog::getToUserId, queryDTO.getToUserId());
        }
        return wrapper;
    }

    private TaskRemindLogVO toRemindLogVO(TaskRemindLog entity) {
        TaskRemindLogVO vo = new TaskRemindLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private TaskTransferLogVO toTransferLogVO(TaskTransferLog entity) {
        TaskTransferLogVO vo = new TaskTransferLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private void saveOperLog(Long userId, Long taskId, String action, String content) {
        SysOperLog log = new SysOperLog();
        log.setUserId(userId);
        log.setBizType(BIZ_TYPE_TASK);
        log.setBizId(taskId);
        log.setAction(action);
        log.setContent(content);
        log.setCreatedAt(LocalDateTime.now());
        sysOperLogMapper.insert(log);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
