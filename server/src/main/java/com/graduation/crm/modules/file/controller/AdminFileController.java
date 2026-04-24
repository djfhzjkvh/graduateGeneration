package com.graduation.crm.modules.file.controller;

import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.file.dto.FileUploadDTO;
import com.graduation.crm.modules.file.service.FileService;
import com.graduation.crm.modules.file.vo.FileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理端文件接口。
 *
 * 用于后台上传楼盘资料、系统附件和演示图片。
 */
@Tag(name = "管理端文件接口")
@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
public class AdminFileController {

    private final FileService fileService;

    /**
     * 管理端文件上传。
     */
    @PostMapping("/upload")
    @Operation(summary = "管理端文件上传")
    public Result<FileVO> upload(@RequestPart("file") MultipartFile file, FileUploadDTO dto) {
        return Result.success(fileService.upload(file, dto));
    }
}

