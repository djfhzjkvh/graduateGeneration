package com.graduation.crm.modules.customer.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.customer.dto.CustomerQueryDTO;
import com.graduation.crm.modules.customer.service.CustomerService;
import com.graduation.crm.modules.customer.vo.CustomerListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端客户接口。
 *
 * 面向销售经理和管理员，用于后台客户总览、团队筛选和后续客户分配。
 * 当前复用客户查询服务，后续可在此扩展导出、分配和批量操作接口。
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
}
