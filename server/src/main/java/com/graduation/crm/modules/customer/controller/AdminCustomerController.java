package com.graduation.crm.modules.customer.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.customer.dto.CustomerAssignDTO;
import com.graduation.crm.modules.customer.dto.CustomerAssignLogQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerExcelImportConfirmDTO;
import com.graduation.crm.modules.customer.dto.CustomerExcelImportPreviewDTO;
import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerStatusUpdateDTO;
import com.graduation.crm.modules.customer.service.CustomerService;
import com.graduation.crm.modules.customer.vo.CustomerAssignLogVO;
import com.graduation.crm.modules.customer.vo.CustomerExcelImportPreviewVO;
import com.graduation.crm.modules.customer.vo.CustomerExcelImportResultVO;
import com.graduation.crm.modules.customer.vo.CustomerListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 管理端客户接口。
 *
 * 面向销售经理和管理员，用于后台客户总览、团队筛选、客户分配/转移、客户阶段管理和 Excel 批量导入。
 */
@Tag(name = "管理端客户接口")
@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final CustomerService customerService;

    /**
     * 管理端客户分页查询，比移动端更偏向全局检索和团队维度筛选。
     */
    @GetMapping
    @Operation(summary = "分页查询客户")
    public Result<PageResult<CustomerListVO>> page(CustomerQueryDTO queryDTO) {
        return Result.success(customerService.page(queryDTO));
    }

    /**
     * Excel 客户导入预览，解析上传文件并返回每一行的校验结果。
     */
    @PostMapping("/import/excel/preview")
    @Operation(summary = "Excel客户导入预览")
    public Result<CustomerExcelImportPreviewVO> previewExcelImport(@RequestBody @Valid CustomerExcelImportPreviewDTO dto) {
        return Result.success(customerService.previewExcelImport(dto));
    }

    /**
     * Excel 客户导入确认，只导入预览中校验通过的客户行。
     */
    @PostMapping("/import/excel/confirm")
    @Operation(summary = "Excel客户导入确认")
    public Result<CustomerExcelImportResultVO> confirmExcelImport(@RequestBody @Valid CustomerExcelImportConfirmDTO dto) {
        return Result.success(customerService.confirmExcelImport(dto));
    }

    /**
     * 查询客户分配/转移日志，方便经理追踪客户资源流转。
     */
    @GetMapping("/assign-logs")
    @Operation(summary = "客户分配日志")
    public Result<PageResult<CustomerAssignLogVO>> assignLogs(CustomerAssignLogQueryDTO queryDTO) {
        return Result.success(customerService.assignLogPage(queryDTO));
    }

    /**
     * 查询单个客户的分配/转移日志。
     */
    @GetMapping("/{customerId}/assign-logs")
    @Operation(summary = "单客户分配日志")
    public Result<PageResult<CustomerAssignLogVO>> assignLogsByCustomer(@PathVariable Long customerId,
                                                                        CustomerAssignLogQueryDTO queryDTO) {
        return Result.success(customerService.assignLogsByCustomer(customerId, queryDTO));
    }

    /**
     * 客户分配/转移，将客户负责人调整为指定顾问，并写入分配日志和操作日志。
     */
    @PutMapping("/{id}/assign")
    @Operation(summary = "客户分配/转移")
    public Result<Void> assign(@PathVariable Long id, @RequestBody @Valid CustomerAssignDTO dto) {
        customerService.assign(id, dto);
        return Result.success();
    }

    /**
     * 管理端客户状态流转，用于经理或管理员修正客户阶段。
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "客户状态流转")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody @Valid CustomerStatusUpdateDTO dto) {
        customerService.updateStatus(id, dto);
        return Result.success();
    }
}
