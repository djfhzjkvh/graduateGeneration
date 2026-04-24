package com.graduation.crm.modules.customer.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.customer.dto.CustomerTagCreateDTO;
import com.graduation.crm.modules.customer.dto.CustomerTagUpdateDTO;
import com.graduation.crm.modules.customer.service.CustomerTagService;
import com.graduation.crm.modules.customer.vo.CustomerTagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端客户标签接口。
 *
 * 用于后台维护系统标签和团队常用标签，移动端只消费启用状态的标签。
 */
@Tag(name = "管理端客户标签接口")
@RestController
@RequestMapping("/api/admin/customer-tags")
@RequiredArgsConstructor
public class AdminCustomerTagController {

    private final CustomerTagService customerTagService;

    /**
     * 查询启用标签列表。
     */
    @GetMapping
    @Operation(summary = "查询客户标签")
    public Result<List<CustomerTagVO>> list() {
        return Result.success(customerTagService.listEnabled());
    }

    /**
     * 新增客户标签，默认启用并标记为自定义标签。
     */
    @PostMapping
    @Operation(summary = "新增客户标签")
    public Result<Void> create(@RequestBody @Valid CustomerTagCreateDTO dto) {
        customerTagService.create(dto);
        return Result.success();
    }

    /**
     * 更新客户标签名称、类型、颜色和状态。
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新客户标签")
    public Result<Void> update(@PathVariable Long id, @RequestBody CustomerTagUpdateDTO dto) {
        customerTagService.update(id, dto);
        return Result.success();
    }

    /**
     * 停用客户标签，保留已有客户标签关系。
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户标签")
    public Result<Void> delete(@PathVariable Long id) {
        customerTagService.delete(id);
        return Result.success();
    }
}

