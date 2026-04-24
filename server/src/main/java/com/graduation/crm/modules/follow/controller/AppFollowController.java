package com.graduation.crm.modules.follow.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.follow.dto.FollowCreateDTO;
import com.graduation.crm.modules.follow.service.FollowService;
import com.graduation.crm.modules.follow.vo.FollowRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 小程序端跟进接口。
 *
 * 跟进记录是客户状态、下次提醒和 AI 复盘的核心数据来源，因此移动端
 * 只开放与客户详情强相关的新增和查询接口。
 */
@Tag(name = "移动端跟进接口")
@RestController
@RequestMapping("/api/app")
@RequiredArgsConstructor
public class AppFollowController {

    private final FollowService followService;

    /**
     * 新增客户跟进记录，同时会更新客户最近跟进时间、下次跟进时间和客户阶段。
     */
    @PostMapping("/follows")
    @Operation(summary = "新增跟进记录")
    public Result<Void> create(@RequestBody @Valid FollowCreateDTO dto) {
        followService.create(dto);
        return Result.success();
    }

    /**
     * 查询指定客户的跟进时间线，用于客户详情页展示历史沟通过程。
     */
    @GetMapping("/customers/{customerId}/follows")
    @Operation(summary = "查询客户跟进记录")
    public Result<List<FollowRecordVO>> listByCustomerId(@PathVariable Long customerId) {
        return Result.success(followService.listByCustomerId(customerId));
    }
}
