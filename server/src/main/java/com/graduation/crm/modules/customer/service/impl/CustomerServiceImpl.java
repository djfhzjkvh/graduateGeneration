package com.graduation.crm.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.admin.entity.SysOperLog;
import com.graduation.crm.modules.admin.mapper.SysOperLogMapper;
import com.graduation.crm.modules.competitor.mapper.CustomerCompetitorFocusMapper;
import com.graduation.crm.modules.competitor.vo.CustomerCompetitorFocusVO;
import com.graduation.crm.modules.customer.dto.CustomerAssignDTO;
import com.graduation.crm.modules.customer.dto.CustomerAssignLogQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerCreateDTO;
import com.graduation.crm.modules.customer.dto.CustomerExcelImportConfirmDTO;
import com.graduation.crm.modules.customer.dto.CustomerExcelImportPreviewDTO;
import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerStatusUpdateDTO;
import com.graduation.crm.modules.customer.dto.CustomerUpdateDTO;
import com.graduation.crm.modules.customer.entity.Customer;
import com.graduation.crm.modules.customer.entity.CustomerAssignLog;
import com.graduation.crm.modules.customer.mapper.CustomerAssignLogMapper;
import com.graduation.crm.modules.customer.mapper.CustomerMapper;
import com.graduation.crm.modules.customer.mapper.CustomerTagMapper;
import com.graduation.crm.modules.customer.service.CustomerService;
import com.graduation.crm.modules.customer.vo.CustomerAssignLogVO;
import com.graduation.crm.modules.customer.vo.CustomerDetailVO;
import com.graduation.crm.modules.customer.vo.CustomerExcelImportPreviewVO;
import com.graduation.crm.modules.customer.vo.CustomerExcelImportResultVO;
import com.graduation.crm.modules.customer.vo.CustomerExcelImportRowVO;
import com.graduation.crm.modules.customer.vo.CustomerListVO;
import com.graduation.crm.modules.customer.vo.CustomerProfileVO;
import com.graduation.crm.modules.file.entity.SysFile;
import com.graduation.crm.modules.file.service.FileService;
import com.graduation.crm.modules.follow.mapper.FollowRecordMapper;
import com.graduation.crm.modules.follow.vo.FollowRecordVO;
import com.graduation.crm.modules.heat.mapper.HeatMapper;
import com.graduation.crm.modules.note.mapper.NoteMapper;
import com.graduation.crm.modules.note.vo.NoteVO;
import com.graduation.crm.modules.task.dto.TaskQueryDTO;
import com.graduation.crm.modules.task.service.TaskService;
import com.graduation.crm.modules.task.vo.TaskVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 客户业务服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final String BIZ_TYPE_CUSTOMER = "CUSTOMER";
    private static final Set<String> VALID_STATUS_SET = new HashSet<>(Arrays.asList(
            "NEW", "FOLLOWING", "VISITED", "DEAL", "LOST"
    ));

    private final CustomerMapper customerMapper;
    private final CustomerTagMapper customerTagMapper;
    private final CustomerAssignLogMapper customerAssignLogMapper;
    private final SysOperLogMapper sysOperLogMapper;
    private final FileService fileService;
    private final FollowRecordMapper followRecordMapper;
    private final NoteMapper noteMapper;
    private final CustomerCompetitorFocusMapper customerCompetitorFocusMapper;
    private final HeatMapper heatMapper;
    private final TaskService taskService;

    @Override
    public PageResult<CustomerListVO> page(CustomerQueryDTO queryDTO) {
        // 列表查询使用 XML，便于维护多条件筛选和关联展示字段。
        Page<CustomerListVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<CustomerListVO> result = customerMapper.selectCustomerPage(page, queryDTO);
        log.debug("Customer page query finished, total={}, pageNum={}, pageSize={}",
                result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public CustomerDetailVO detail(Long id) {
        CustomerDetailVO detail = customerMapper.selectCustomerDetail(id);
        if (detail == null) {
            throw new BusinessException("客户不存在");
        }
        // 客户详情单独补充标签列表，避免主查询因多标签导致客户信息重复。
        detail.setTagNames(customerTagMapper.selectTagNamesByCustomerId(id));
        log.debug("Customer detail query finished, id={}", id);
        return detail;
    }

    @Override
    public CustomerProfileVO profile(Long id) {
        CustomerProfileVO profile = new CustomerProfileVO();
        profile.setCustomer(detail(id));
        profile.setRecentFollows(limitList(followRecordMapper.selectByCustomerId(id), 5));
        profile.setRecentNotes(limitList(noteMapper.selectByCustomerId(id), 5));
        profile.setCompetitorFocuses(limitList(customerCompetitorFocusMapper.selectByCustomerId(id), 5));
        profile.setAssignLogs(limitList(customerAssignLogMapper.selectList(new LambdaQueryWrapper<CustomerAssignLog>()
                .eq(CustomerAssignLog::getCustomerId, id)
                .orderByDesc(CustomerAssignLog::getCreatedAt)).stream()
                .map(this::toAssignLogVO)
                .collect(Collectors.toList()), 5));
        profile.setHeat(heatMapper.selectLatestHeat(id));

        TaskQueryDTO taskQueryDTO = new TaskQueryDTO();
        taskQueryDTO.setCustomerId(id);
        taskQueryDTO.setPageNum(1);
        taskQueryDTO.setPageSize(5);
        PageResult<TaskVO> taskPage = taskService.page(taskQueryDTO);
        profile.setRecentTasks(taskPage.getList());
        return profile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CustomerCreateDTO dto) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(dto, customer);
        // 新建线索统一从 NEW 状态开始，热度分后续由规则或 AI 模块刷新。
        customer.setStatus("NEW");
        customer.setHeatScore(0);
        customer.setDeleted(0);
        customerMapper.insert(customer);
        saveOperLog(customer.getCreatedBy(), customer.getId(), "CUSTOMER_CREATE", "创建客户：" + customer.getCustomerName());
        log.info("Customer created, id={}", customer.getId());
        return customer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, CustomerUpdateDTO dto) {
        Customer customer = getAvailableCustomer(id);
        BeanUtils.copyProperties(dto, customer);
        customerMapper.updateById(customer);
        saveOperLog(customer.getCreatedBy(), id, "CUSTOMER_UPDATE", "更新客户资料：" + customer.getCustomerName());
        log.info("Customer updated, id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Customer customer = getAvailableCustomer(id);
        // 客户数据涉及跟进和任务链路，默认采用逻辑删除保留历史记录。
        customer.setDeleted(1);
        customerMapper.updateById(customer);
        saveOperLog(customer.getCreatedBy(), id, "CUSTOMER_DELETE", "删除客户：" + customer.getCustomerName());
        log.info("Customer logically deleted, id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long id, CustomerAssignDTO dto) {
        Customer customer = getAvailableCustomer(id);
        Long fromUserId = customer.getAdvisorId();

        customer.setAdvisorId(dto.getToUserId());
        if (dto.getManagerId() != null) {
            customer.setManagerId(dto.getManagerId());
        }
        if (dto.getDeptId() != null) {
            customer.setDeptId(dto.getDeptId());
        }
        customer.setUpdatedAt(LocalDateTime.now());
        customerMapper.updateById(customer);

        CustomerAssignLog assignLog = new CustomerAssignLog();
        assignLog.setCustomerId(id);
        assignLog.setFromUserId(fromUserId);
        assignLog.setToUserId(dto.getToUserId());
        assignLog.setActionType(normalizeActionType(dto.getActionType(), fromUserId));
        assignLog.setRemark(dto.getRemark());
        assignLog.setCreatedBy(dto.getCreatedBy());
        assignLog.setCreatedAt(LocalDateTime.now());
        customerAssignLogMapper.insert(assignLog);

        saveOperLog(dto.getCreatedBy(), id, "CUSTOMER_ASSIGN",
                "客户分配/转移：fromUserId=" + fromUserId + ", toUserId=" + dto.getToUserId()
                        + "，备注：" + nullToEmpty(dto.getRemark()));
        log.info("Customer assigned, customerId={}, fromUserId={}, toUserId={}", id, fromUserId, dto.getToUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, CustomerStatusUpdateDTO dto) {
        String targetStatus = dto.getStatus().trim().toUpperCase();
        if (!VALID_STATUS_SET.contains(targetStatus)) {
            throw new BusinessException("客户状态不合法");
        }

        Customer customer = getAvailableCustomer(id);
        String oldStatus = customer.getStatus();
        customer.setStatus(targetStatus);
        customer.setUpdatedAt(LocalDateTime.now());
        customerMapper.updateById(customer);

        saveOperLog(dto.getOperatorId(), id, "CUSTOMER_STATUS_CHANGE",
                "客户状态流转：" + oldStatus + " -> " + targetStatus + "，备注：" + nullToEmpty(dto.getRemark()));
        log.info("Customer status changed, customerId={}, oldStatus={}, newStatus={}", id, oldStatus, targetStatus);
    }

    @Override
    public PageResult<CustomerAssignLogVO> assignLogPage(CustomerAssignLogQueryDTO queryDTO) {
        Page<CustomerAssignLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<CustomerAssignLog> wrapper = buildAssignLogWrapper(queryDTO);
        IPage<CustomerAssignLog> result = customerAssignLogMapper.selectPage(page, wrapper);
        List<CustomerAssignLogVO> list = result.getRecords().stream()
                .map(this::toAssignLogVO)
                .collect(Collectors.toList());
        return PageResult.of(list, result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public PageResult<CustomerAssignLogVO> assignLogsByCustomer(Long customerId, CustomerAssignLogQueryDTO queryDTO) {
        queryDTO.setCustomerId(customerId);
        return assignLogPage(queryDTO);
    }

    @Override
    public CustomerExcelImportPreviewVO previewExcelImport(CustomerExcelImportPreviewDTO dto) {
        List<CustomerExcelImportRowVO> rows = parseExcelRows(dto.getSourceFileId());
        CustomerExcelImportPreviewVO preview = new CustomerExcelImportPreviewVO();
        preview.setSourceFileId(dto.getSourceFileId());
        preview.setRows(rows);
        preview.setTotalCount(rows.size());
        preview.setValidCount((int) rows.stream().filter(CustomerExcelImportRowVO::getValid).count());
        preview.setInvalidCount(preview.getTotalCount() - preview.getValidCount());
        return preview;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerExcelImportResultVO confirmExcelImport(CustomerExcelImportConfirmDTO dto) {
        List<CustomerExcelImportRowVO> rows = parseExcelRows(dto.getSourceFileId());
        List<CustomerExcelImportRowVO> failedRows = new ArrayList<>();
        int successCount = 0;

        for (CustomerExcelImportRowVO row : rows) {
            if (!Boolean.TRUE.equals(row.getValid())) {
                failedRows.add(row);
                continue;
            }
            try {
                row.getCustomer().setCreatedBy(dto.getCreatedBy());
                create(row.getCustomer());
                successCount++;
            } catch (Exception e) {
                row.setValid(false);
                row.getErrors().add(e.getMessage());
                failedRows.add(row);
            }
        }

        CustomerExcelImportResultVO result = new CustomerExcelImportResultVO();
        result.setSuccessCount(successCount);
        result.setFailedCount(failedRows.size());
        result.setFailedRows(failedRows);
        saveOperLog(dto.getCreatedBy(), dto.getSourceFileId(), "CUSTOMER_EXCEL_IMPORT",
                "Excel导入客户：成功 " + successCount + " 条，失败 " + failedRows.size() + " 条");
        return result;
    }

    private Customer getAvailableCustomer(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null || Integer.valueOf(1).equals(customer.getDeleted())) {
            throw new BusinessException("客户不存在");
        }
        return customer;
    }

    private LambdaQueryWrapper<CustomerAssignLog> buildAssignLogWrapper(CustomerAssignLogQueryDTO queryDTO) {
        LambdaQueryWrapper<CustomerAssignLog> wrapper = new LambdaQueryWrapper<CustomerAssignLog>()
                .orderByDesc(CustomerAssignLog::getCreatedAt);
        if (queryDTO.getCustomerId() != null) {
            wrapper.eq(CustomerAssignLog::getCustomerId, queryDTO.getCustomerId());
        }
        if (queryDTO.getToUserId() != null) {
            wrapper.eq(CustomerAssignLog::getToUserId, queryDTO.getToUserId());
        }
        if (queryDTO.getActionType() != null && !"".equals(queryDTO.getActionType().trim())) {
            wrapper.eq(CustomerAssignLog::getActionType, queryDTO.getActionType());
        }
        return wrapper;
    }

    private CustomerAssignLogVO toAssignLogVO(CustomerAssignLog entity) {
        CustomerAssignLogVO vo = new CustomerAssignLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private String normalizeActionType(String actionType, Long fromUserId) {
        if (actionType != null && !"".equals(actionType.trim())) {
            return actionType.trim().toUpperCase();
        }
        return fromUserId == null ? "ASSIGN" : "TRANSFER";
    }

    private void saveOperLog(Long userId, Long customerId, String action, String content) {
        SysOperLog log = new SysOperLog();
        log.setUserId(userId);
        log.setBizType(BIZ_TYPE_CUSTOMER);
        log.setBizId(customerId);
        log.setAction(action);
        log.setContent(content);
        log.setCreatedAt(LocalDateTime.now());
        sysOperLogMapper.insert(log);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private <T> List<T> limitList(List<T> list, int limit) {
        if (list == null || list.size() <= limit) {
            return list;
        }
        return list.subList(0, limit);
    }

    private List<CustomerExcelImportRowVO> parseExcelRows(Long sourceFileId) {
        SysFile sysFile = fileService.getEntity(sourceFileId);
        validateExcelFile(sysFile);

        File file = fileService.resolveLocalFile(sourceFileId);
        try (FileInputStream inputStream = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                return new ArrayList<>();
            }

            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> headerMap = resolveHeaderMap(sheet.getRow(0), formatter);
            List<CustomerExcelImportRowVO> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isBlankRow(row, formatter)) {
                    continue;
                }
                rows.add(parseCustomerRow(i + 1, row, headerMap, formatter));
            }
            return rows;
        } catch (Exception e) {
            throw new BusinessException("解析Excel失败：" + e.getMessage());
        }
    }

    private CustomerExcelImportRowVO parseCustomerRow(Integer rowIndex, Row row, Map<String, Integer> headerMap, DataFormatter formatter) {
        CustomerCreateDTO customer = new CustomerCreateDTO();
        customer.setCustomerName(cellValue(row, headerMap, formatter, "customerName", "客户姓名", "姓名", "name"));
        customer.setMobile(cellValue(row, headerMap, formatter, "mobile", "手机号", "电话", "手机", "phone"));
        customer.setGender(cellValue(row, headerMap, formatter, "gender", "性别"));
        customer.setSource(cellValue(row, headerMap, formatter, "source", "来源", "客户来源"));
        customer.setIntentLevel(cellValue(row, headerMap, formatter, "intentLevel", "意向等级", "意向"));
        customer.setBudgetMin(decimalValue(row, headerMap, formatter, "budgetMin", "预算下限", "最低预算"));
        customer.setBudgetMax(decimalValue(row, headerMap, formatter, "budgetMax", "预算上限", "最高预算"));
        customer.setRegion(cellValue(row, headerMap, formatter, "region", "区域", "意向区域"));
        customer.setHouseType(cellValue(row, headerMap, formatter, "houseType", "户型", "意向户型"));
        customer.setPurpose(cellValue(row, headerMap, formatter, "purpose", "购房目的", "目的"));
        customer.setRemark(cellValue(row, headerMap, formatter, "remark", "备注"));
        customer.setAdvisorId(longValue(row, headerMap, formatter, "advisorId", "顾问ID", "负责人ID"));
        customer.setManagerId(longValue(row, headerMap, formatter, "managerId", "经理ID"));
        customer.setDeptId(longValue(row, headerMap, formatter, "deptId", "部门ID"));

        List<String> errors = new ArrayList<>();
        if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
            errors.add("客户姓名不能为空");
        }

        CustomerExcelImportRowVO vo = new CustomerExcelImportRowVO();
        vo.setRowIndex(rowIndex);
        vo.setCustomer(customer);
        vo.setErrors(errors);
        vo.setValid(errors.isEmpty());
        return vo;
    }

    private void validateExcelFile(SysFile sysFile) {
        String fileName = sysFile.getFileName() == null ? "" : sysFile.getFileName().toLowerCase();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            throw new BusinessException("只支持 xlsx/xls Excel 文件");
        }
    }

    private Map<String, Integer> resolveHeaderMap(Row headerRow, DataFormatter formatter) {
        Map<String, Integer> headerMap = new HashMap<>();
        if (headerRow == null) {
            return headerMap;
        }
        for (Cell cell : headerRow) {
            String header = formatter.formatCellValue(cell);
            if (header != null && !header.trim().isEmpty()) {
                headerMap.put(header.trim(), cell.getColumnIndex());
            }
        }
        return headerMap;
    }

    private boolean isBlankRow(Row row, DataFormatter formatter) {
        for (Cell cell : row) {
            if (formatter.formatCellValue(cell) != null && !formatter.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String cellValue(Row row, Map<String, Integer> headerMap, DataFormatter formatter, String... aliases) {
        for (String alias : aliases) {
            Integer index = headerMap.get(alias);
            if (index != null) {
                String value = formatter.formatCellValue(row.getCell(index));
                return value == null || value.trim().isEmpty() ? null : value.trim();
            }
        }
        return null;
    }

    private BigDecimal decimalValue(Row row, Map<String, Integer> headerMap, DataFormatter formatter, String... aliases) {
        String value = cellValue(row, headerMap, formatter, aliases);
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value.replace(",", ""));
        } catch (Exception e) {
            return null;
        }
    }

    private Long longValue(Row row, Map<String, Integer> headerMap, DataFormatter formatter, String... aliases) {
        String value = cellValue(row, headerMap, formatter, aliases);
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(value.replace(",", "")).longValue();
        } catch (Exception e) {
            return null;
        }
    }
}
