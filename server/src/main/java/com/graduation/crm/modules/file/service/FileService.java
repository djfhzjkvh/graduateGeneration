package com.graduation.crm.modules.file.service;

import com.graduation.crm.modules.file.dto.FileUploadDTO;
import com.graduation.crm.modules.file.entity.SysFile;
import com.graduation.crm.modules.file.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileVO upload(MultipartFile file, FileUploadDTO dto);

    FileVO detail(Long id);

    SysFile getEntity(Long id);

    String readImageAsDataUrl(Long id);
}

