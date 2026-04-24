package com.graduation.crm.modules.customer.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.customer.dto.CustomerCreateDTO;
import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import com.graduation.crm.modules.customer.dto.CustomerStatusUpdateDTO;
import com.graduation.crm.modules.customer.dto.CustomerUpdateDTO;
import com.graduation.crm.modules.customer.service.CustomerService;
import com.graduation.crm.modules.customer.vo.CustomerDetailVO;
import com.graduation.crm.modules.customer.vo.CustomerListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 小程序端客户接口。
 *
 * 面向置业顾问和销售经理的移动端客户操作，后续接入登录后需要在数据权限层限制访问范围。
 */
@Tag(name = "移动端客户接口")
@Validated
@RestController
@RequestMapping("/api/app/customers")
@RequiredArgsConstructor
public class AppCustomerController {

    private final CustomerService customerService;

    /**
     * 查询移动端客户列表，支持关键字、状态、负责人和热度区间筛选。
     */
    @GetMapping
    @Operation(summary = "分页查询客户")
    public Result<PageResult<CustomerListVO>> page(CustomerQueryDTO queryDTO) {
        return Result.success(customerService.page(queryDTO));
    }

    /**
     * 查询客户详情，用于客户详情页展示基础信息、负责人和热度等核心字段。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询客户详情")
    public Result<CustomerDetailVO> detail(@PathVariable Long id) {
        return Result.success(customerService.detail(id));
    }

    /**
     * 新增客户线索，默认进入 NEW 状态，后续由跟进记录推动客户阶段变化。
     */
    @PostMapping
    @Operation(summary = "新增客户")
    public Result<Void> create(@RequestBody @Valid CustomerCreateDTO dto) {
        customerService.create(dto);
        return Result.success();
    }

    /**
     * 更新客户资料，主要用于顾问补充预算、户型、区域和标签等信息。
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新客户")
    public Result<Void> update(@PathVariable Long id, @RequestBody CustomerUpdateDTO dto) {
        customerService.update(id, dto);
        return Result.success();
    }

    /**
     * 客户状态流转，用于顾问在跟进后推进客户阶段。
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "客户状态流转")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody @Valid CustomerStatusUpdateDTO dto) {
        customerService.updateStatus(id, dto);
        return Result.success();
    }

    /**
     * 删除客户采用逻辑删除，避免影响已有跟进、任务和报表统计链路。
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success();
    }
}
