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
 * 小程序端文件接口。
 *
 * 用于上传聊天截图、跟进附件等移动端文件。
 */
@Tag(name = "移动端文件接口")
@RestController
@RequestMapping("/api/app/files")
@RequiredArgsConstructor
public class AppFileController {

    private final FileService fileService;

    /**
     * 上传文件并返回可预览的本地静态资源地址。
     */
    @PostMapping("/upload")
    @Operation(summary = "移动端文件上传")
    public Result<FileVO> upload(@RequestPart("file") MultipartFile file, FileUploadDTO dto) {
        return Result.success(fileService.upload(file, dto));
    }
}

