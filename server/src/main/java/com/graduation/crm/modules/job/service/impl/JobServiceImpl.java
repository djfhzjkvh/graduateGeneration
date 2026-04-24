package com.graduation.crm.modules.job.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.graduation.crm.modules.admin.service.SystemConfigService;
import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.customer.mapper.CustomerMapper;
import com.graduation.crm.modules.job.service.JobService;
import com.graduation.crm.modules.job.vo.JobRunResultVO;
import com.graduation.crm.modules.message.entity.SysMessage;
import com.graduation.crm.modules.message.mapper.MessageMapper;
import com.graduation.crm.modules.report.entity.ReportDailyStat;
import com.graduation.crm.modules.report.mapper.ReportDailyStatMapper;
import com.graduation.crm.modules.task.entity.Task;
import com.graduation.crm.modules.task.entity.TaskRemindLog;
import com.graduation.crm.modules.task.mapper.TaskMapper;
import com.graduation.crm.modules.task.mapper.TaskRemindLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统自动化任务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_OVERDUE = "OVERDUE";
    private static final String TASK_TYPE_FOLLOW = "FOLLOW";
    private static final String SOURCE_CUSTOMER_NEXT_FOLLOW = "CUSTOMER_NEXT_FOLLOW";
    private static final String REMIND_TYPE_IN_APP = "IN_APP";
    private static final int DUE_SOON_WINDOW_HOURS = 2;

    private final TaskMapper taskMapper;
    private final TaskRemindLogMapper taskRemindLogMapper;
    private final CustomerMapper customerMapper;
    private final MessageMapper messageMapper;
    private final ReportDailyStatMapper reportDailyStatMapper;
    private final SystemConfigService systemConfigService;

    /**
     * 自动刷新逾期任务：将今天之前仍未完成的待办任务标记为 OVERDUE，并生成站内提醒。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobRunResultVO refreshOverdueTasks() {
        LocalDate today = LocalDate.now();
        List<Task> overdueTasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getDeleted, 0)
                .eq(Task::getStatus, STATUS_PENDING)
                .lt(Task::getTaskDate, today));

        if (overdueTasks.isEmpty()) {
            log.info("自动刷新逾期任务完成：没有需要处理的任务");
            return new JobRunResultVO("refreshOverdueTasks", 0, "没有需要刷新为逾期的任务");
        }

        List<Long> taskIds = overdueTasks.stream().map(Task::getId).collect(Collectors.toList());
        taskMapper.update(null, new LambdaUpdateWrapper<Task>()
                .in(Task::getId, taskIds)
                .set(Task::getStatus, STATUS_OVERDUE)
                .set(Task::getUpdatedAt, LocalDateTime.now()));

        overdueTasks.forEach(task -> messageMapper.insert(buildTaskMessage(
                task.getOwnerId(),
                "逾期任务提醒",
                "任务「" + task.getTitle() + "」已逾期，请尽快处理。",
                task.getId()
        )));

        log.info("自动刷新逾期任务完成：处理 {} 条", overdueTasks.size());
        return new JobRunResultVO("refreshOverdueTasks", overdueTasks.size(), "逾期任务刷新完成");
    }

    /**
     * 自动生成跟进提醒任务：根据客户 next_follow_time 生成当天应跟进的任务，并通过 source_type/source_id 防重复。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobRunResultVO generateFollowTasks() {
        LocalDate today = LocalDate.now();
        List<Customer> customers = customerMapper.selectList(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getDeleted, 0)
                .isNotNull(Customer::getAdvisorId)
                .isNotNull(Customer::getNextFollowTime)
                .le(Customer::getNextFollowTime, today.atTime(LocalTime.MAX)));

        int createdCount = 0;
        for (Customer customer : customers) {
            LocalDate taskDate = customer.getNextFollowTime().toLocalDate();
            Long existingCount = taskMapper.selectCount(new LambdaQueryWrapper<Task>()
                    .eq(Task::getDeleted, 0)
                    .eq(Task::getCustomerId, customer.getId())
                    .eq(Task::getOwnerId, customer.getAdvisorId())
                    .eq(Task::getSourceType, SOURCE_CUSTOMER_NEXT_FOLLOW)
                    .eq(Task::getSourceId, customer.getId())
                    .eq(Task::getTaskDate, taskDate)
                    .in(Task::getStatus, STATUS_PENDING, STATUS_OVERDUE));

            if (existingCount > 0) {
                continue;
            }

            Task task = buildFollowTask(customer, taskDate);
            taskMapper.insert(task);
            messageMapper.insert(buildTaskMessage(
                    customer.getAdvisorId(),
                    "客户跟进提醒",
                    "客户「" + customer.getCustomerName() + "」已到计划跟进时间，请及时联系。",
                    task.getId()
            ));
            createdCount++;
        }

        log.info("自动生成跟进提醒任务完成：新增 {} 条", createdCount);
        return new JobRunResultVO("generateFollowTasks", createdCount, "跟进提醒任务生成完成");
    }

    /**
     * 即将到期任务提醒：扫描未来 2 小时内的待办任务，生成站内消息并写提醒日志防重复。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobRunResultVO remindDueSoonTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusHours(DUE_SOON_WINDOW_HOURS);
        List<Task> pendingTasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getDeleted, 0)
                .eq(Task::getStatus, STATUS_PENDING)
                .ge(Task::getTaskDate, now.toLocalDate())
                .le(Task::getTaskDate, deadline.toLocalDate()));

        int remindCount = 0;
        for (Task task : pendingTasks) {
            LocalDateTime taskDateTime = combineTaskDateTime(task);
            if (taskDateTime.isBefore(now) || taskDateTime.isAfter(deadline) || hasSuccessRemindLog(task.getId())) {
                continue;
            }

            messageMapper.insert(buildTaskMessage(
                    task.getOwnerId(),
                    "任务即将到期",
                    "任务「" + task.getTitle() + "」将在 " + DUE_SOON_WINDOW_HOURS + " 小时内到期，请及时处理。",
                    task.getId()
            ));
            taskRemindLogMapper.insert(buildRemindLog(task.getId(), "SUCCESS", "站内消息提醒已生成"));
            remindCount++;
        }

        log.info("即将到期任务提醒完成：提醒 {} 条", remindCount);
        return new JobRunResultVO("remindDueSoonTasks", remindCount, "即将到期任务提醒完成");
    }

    /**
     * 每日统计报表生成：按顾问维度汇总客户、任务和预估金额，写入 report_daily_stat。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobRunResultVO generateDailyReport() {
        LocalDate today = LocalDate.now();
        List<Customer> customers = customerMapper.selectList(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getDeleted, 0)
                .isNotNull(Customer::getAdvisorId));
        List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
                .eq(Task::getDeleted, 0)
                .isNotNull(Task::getOwnerId));

        List<Long> userIds = new ArrayList<>();
        customers.stream().map(Customer::getAdvisorId).distinct().forEach(userIds::add);
        tasks.stream().map(Task::getOwnerId).distinct().filter(id -> !userIds.contains(id)).forEach(userIds::add);

        Map<Long, List<Customer>> customerMap = customers.stream().collect(Collectors.groupingBy(Customer::getAdvisorId));
        Map<Long, List<Task>> taskMap = tasks.stream().collect(Collectors.groupingBy(Task::getOwnerId));

        int generatedCount = 0;
        for (Long userId : userIds) {
            List<Customer> userCustomers = customerMap.getOrDefault(userId, new ArrayList<>());
            List<Task> userTasks = taskMap.getOrDefault(userId, new ArrayList<>());
            ReportDailyStat stat = buildDailyStat(today, userId, userCustomers, userTasks);

            // 先删后插，保证同一天同一顾问只有一条最新快照，避免重复报表数据。
            reportDailyStatMapper.delete(new LambdaQueryWrapper<ReportDailyStat>()
                    .eq(ReportDailyStat::getStatDate, today)
                    .eq(ReportDailyStat::getUserId, userId));
            reportDailyStatMapper.insert(stat);
            generatedCount++;
        }

        log.info("每日统计报表生成完成：生成 {} 条顾问日报", generatedCount);
        return new JobRunResultVO("generateDailyReport", generatedCount, "每日统计报表生成完成");
    }

    private Task buildFollowTask(Customer customer, LocalDate taskDate) {
        Integer highIntentThreshold = systemConfigService.getHighIntentThreshold();
        Task task = new Task();
        task.setCustomerId(customer.getId());
        task.setTaskType(TASK_TYPE_FOLLOW);
        task.setTitle("客户回访提醒");
        task.setContent("客户「" + customer.getCustomerName() + "」计划跟进时间已到，请补充本次跟进记录。");
        task.setTaskDate(taskDate);
        task.setTaskTime(customer.getNextFollowTime().toLocalTime());
        task.setStatus(STATUS_PENDING);
        task.setPriority(isHighIntent(customer, highIntentThreshold) ? "HIGH" : "MEDIUM");
        task.setOwnerId(customer.getAdvisorId());
        task.setSourceType(SOURCE_CUSTOMER_NEXT_FOLLOW);
        task.setSourceId(customer.getId());
        task.setDelayCount(0);
        task.setDeleted(0);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }

    private SysMessage buildTaskMessage(Long userId, String title, String content, Long taskId) {
        SysMessage message = new SysMessage();
        message.setUserId(userId);
        message.setMsgType("TASK");
        message.setTitle(title);
        message.setContent(content);
        message.setBizType("TASK");
        message.setBizId(taskId);
        message.setReadStatus(0);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    private LocalDateTime combineTaskDateTime(Task task) {
        LocalTime taskTime = task.getTaskTime() == null ? LocalTime.of(23, 59, 59) : task.getTaskTime();
        return LocalDateTime.of(task.getTaskDate(), taskTime);
    }

    private boolean hasSuccessRemindLog(Long taskId) {
        Long count = taskRemindLogMapper.selectCount(new LambdaQueryWrapper<TaskRemindLog>()
                .eq(TaskRemindLog::getTaskId, taskId)
                .eq(TaskRemindLog::getRemindType, REMIND_TYPE_IN_APP)
                .eq(TaskRemindLog::getStatus, "SUCCESS"));
        return count > 0;
    }

    private TaskRemindLog buildRemindLog(Long taskId, String status, String resultMsg) {
        TaskRemindLog log = new TaskRemindLog();
        log.setTaskId(taskId);
        log.setRemindType(REMIND_TYPE_IN_APP);
        log.setRemindTime(LocalDateTime.now());
        log.setStatus(status);
        log.setResultMsg(resultMsg);
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }

    private ReportDailyStat buildDailyStat(LocalDate today, Long userId, List<Customer> customers, List<Task> tasks) {
        Integer highIntentThreshold = systemConfigService.getHighIntentThreshold();
        ReportDailyStat stat = new ReportDailyStat();
        stat.setStatDate(today);
        stat.setUserId(userId);
        stat.setDeptId(resolveDeptId(customers));
        stat.setNewCustomerCount(countNewCustomers(today, customers));
        stat.setHighIntentCount(countHighIntentCustomers(customers, highIntentThreshold));
        stat.setPendingTaskCount(countTasksByStatus(tasks, STATUS_PENDING));
        stat.setForgetTaskCount(countTasksByStatus(tasks, STATUS_OVERDUE));
        stat.setExpectedAmount(sumExpectedAmount(customers, highIntentThreshold));
        stat.setCreatedAt(LocalDateTime.now());
        return stat;
    }

    private Long resolveDeptId(List<Customer> customers) {
        return customers.stream()
                .map(Customer::getDeptId)
                .filter(deptId -> deptId != null)
                .findFirst()
                .orElse(null);
    }

    private Integer countNewCustomers(LocalDate today, List<Customer> customers) {
        return (int) customers.stream()
                .filter(customer -> customer.getCreatedAt() != null)
                .filter(customer -> today.equals(customer.getCreatedAt().toLocalDate()))
                .count();
    }

    private Integer countHighIntentCustomers(List<Customer> customers, Integer highIntentThreshold) {
        return (int) customers.stream()
                .filter(customer -> isHighIntent(customer, highIntentThreshold))
                .count();
    }

    private Integer countTasksByStatus(List<Task> tasks, String status) {
        return (int) tasks.stream()
                .filter(task -> status.equals(task.getStatus()))
                .count();
    }

    private BigDecimal sumExpectedAmount(List<Customer> customers, Integer highIntentThreshold) {
        return customers.stream()
                .filter(customer -> isHighIntent(customer, highIntentThreshold))
                .map(Customer::getBudgetMax)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isHighIntent(Customer customer, Integer highIntentThreshold) {
        return customer.getHeatScore() != null && customer.getHeatScore() >= highIntentThreshold;
    }
}
