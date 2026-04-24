package com.graduation.crm.modules.file.service.impl;

import com.graduation.crm.common.config.FileProperties;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.file.dto.FileUploadDTO;
import com.graduation.crm.modules.file.entity.SysFile;
import com.graduation.crm.modules.file.mapper.SysFileMapper;
import com.graduation.crm.modules.file.service.FileService;
import com.graduation.crm.modules.file.vo.FileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

/**
 * 本地文件上传与读取服务。
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");

    private final FileProperties fileProperties;
    private final SysFileMapper sysFileMapper;

    @Override
    public FileVO upload(MultipartFile file, FileUploadDTO dto) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        try {
            String monthPath = LocalDate.now().format(MONTH_FORMATTER);
            File dir = new File(fileProperties.getUploadDir(), monthPath);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new BusinessException("上传目录创建失败");
            }

            String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String ext = resolveExt(originalName);
            String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
            File target = new File(dir, storedName);
            file.transferTo(target);

            SysFile sysFile = new SysFile();
            sysFile.setBizType(dto.getBizType());
            sysFile.setBizId(dto.getBizId());
            sysFile.setFileName(originalName);
            sysFile.setFileType(file.getContentType());
            sysFile.setFileSize(file.getSize());
            sysFile.setCreatedBy(dto.getCreatedBy());
            sysFile.setFileUrl(buildFileUrl(monthPath, storedName));
            sysFileMapper.insert(sysFile);
            return toVO(sysFile);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public FileVO detail(Long id) {
        return toVO(getEntity(id));
    }

    @Override
    public SysFile getEntity(Long id) {
        SysFile sysFile = sysFileMapper.selectById(id);
        if (sysFile == null) {
            throw new BusinessException("文件不存在");
        }
        return sysFile;
    }

    @Override
    public String readImageAsDataUrl(Long id) {
        SysFile sysFile = getEntity(id);
        if (sysFile.getFileType() == null || !sysFile.getFileType().startsWith("image/")) {
            throw new BusinessException("当前文件不是图片，不能用于图片智能识别");
        }
        try {
            File file = resolveLocalFile(sysFile);
            byte[] bytes = Files.readAllBytes(file.toPath());
            return "data:" + sysFile.getFileType() + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new BusinessException("读取图片文件失败：" + e.getMessage());
        }
    }

    @Override
    public String readAudioAsBase64(Long id) {
        SysFile sysFile = getEntity(id);
        if (sysFile.getFileType() == null || !sysFile.getFileType().startsWith("audio/")) {
            throw new BusinessException("当前文件不是音频，不能用于语音智能识别");
        }
        try {
            File file = resolveLocalFile(sysFile);
            byte[] bytes = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new BusinessException("读取音频文件失败：" + e.getMessage());
        }
    }

    @Override
    public String resolveAudioFormat(Long id) {
        SysFile sysFile = getEntity(id);
        String fileName = sysFile.getFileName() == null ? "" : sysFile.getFileName().toLowerCase();
        String fileType = sysFile.getFileType() == null ? "" : sysFile.getFileType().toLowerCase();
        if (fileName.endsWith(".mp3") || fileType.contains("mpeg")) {
            return "mp3";
        }
        if (fileName.endsWith(".m4a") || fileType.contains("mp4")) {
            return "m4a";
        }
        if (fileName.endsWith(".ogg") || fileType.contains("ogg")) {
            return "ogg";
        }
        return "wav";
    }

    @Override
    public File resolveLocalFile(Long id) {
        return resolveLocalFile(getEntity(id));
    }

    private File resolveLocalFile(SysFile sysFile) {
        String prefix = fileProperties.getAccessPrefix();
        String relativePath = sysFile.getFileUrl().startsWith(prefix)
                ? sysFile.getFileUrl().substring(prefix.length())
                : sysFile.getFileUrl();
        relativePath = relativePath.replaceFirst("^/+", "").replace("/", File.separator);
        return new File(fileProperties.getUploadDir(), relativePath);
    }

    private String resolveExt(String fileName) {
        int index = fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index) : "";
    }

    private String buildFileUrl(String monthPath, String storedName) {
        return fileProperties.getAccessPrefix() + "/" + monthPath + "/" + storedName;
    }

    private FileVO toVO(SysFile sysFile) {
        FileVO vo = new FileVO();
        BeanUtils.copyProperties(sysFile, vo);
        return vo;
    }
}
