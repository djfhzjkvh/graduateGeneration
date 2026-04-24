package com.graduation.crm.modules.message.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.message.dto.MessageQueryDTO;
import com.graduation.crm.modules.message.service.MessageService;
import com.graduation.crm.modules.message.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序端消息中心接口。
 *
 * 用于顾问和经理查看任务提醒、高意向提醒和系统通知。
 */
@Tag(name = "移动端消息接口")
@RestController
@RequestMapping("/api/app/messages")
@RequiredArgsConstructor
public class AppMessageController {

    private final MessageService messageService;

    /**
     * 分页查询当前用户消息，现阶段通过 userId 参数模拟当前登录用户。
     */
    @GetMapping
    @Operation(summary = "分页查询消息")
    public Result<PageResult<MessageVO>> page(MessageQueryDTO queryDTO) {
        return Result.success(messageService.page(queryDTO));
    }

    /**
     * 将单条消息标记为已读。
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "标记消息已读")
    public Result<Void> read(@PathVariable Long id) {
        messageService.read(id);
        return Result.success();
    }

    /**
     * 将当前用户全部未读消息标记为已读。
     */
    @PutMapping("/read-all")
    @Operation(summary = "全部消息已读")
    public Result<Void> readAll(@RequestParam Long userId) {
        messageService.readAll(userId);
        return Result.success();
    }
}

