package com.graduation.crm.modules.file.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.file.service.FileService;
import com.graduation.crm.modules.file.vo.FileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通用文件信息接口。
 *
 * 静态资源内容由 /uploads/** 直接访问，此接口只返回数据库中的文件元数据。
 */
@Tag(name = "通用文件接口")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 查询文件元数据。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询文件信息")
    public Result<FileVO> detail(@PathVariable Long id) {
        return Result.success(fileService.detail(id));
    }
}

