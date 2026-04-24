package com.graduation.crm.modules.customer.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.customer.dto.CustomerTagBindDTO;
import com.graduation.crm.modules.customer.service.CustomerTagService;
import com.graduation.crm.modules.customer.vo.CustomerTagVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序端客户标签接口。
 *
 * 用于客户详情页展示可选标签，并支持顾问给客户绑定标签。
 */
@Tag(name = "移动端客户标签接口")
@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppCustomerTagController {

    private final CustomerTagService customerTagService;

    /**
     * 查询启用标签列表，用于客户表单和详情页标签选择。
     */
    @GetMapping("/customer-tags")
    @Operation(summary = "查询客户标签")
    public Result<List<CustomerTagVO>> list() {
        return Result.success(customerTagService.listEnabled());
    }

    /**
     * 绑定客户标签，提交的标签集合会覆盖客户当前标签。
     */
    @PutMapping("/customers/{customerId}/tags")
    @Operation(summary = "绑定客户标签")
    public Result<Void> bindCustomerTags(@PathVariable Long customerId, @RequestBody CustomerTagBindDTO dto) {
        customerTagService.bindCustomerTags(customerId, dto);
        return Result.success();
    }
}

