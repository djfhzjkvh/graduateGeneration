package com.graduation.crm.modules.file.service.impl;

import com.graduation.crm.common.config.FileProperties;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.modules.file.dto.FileUploadDTO;
import com.graduation.crm.modules.file.entity.SysFile;
import com.graduation.crm.modules.file.mapper.SysFileMapper;
import com.graduation.crm.modules.file.service.FileService;
import com.graduation.crm.modules.file.vo.FileVO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public String readAudioAsDataUrl(Long id) {
        SysFile sysFile = getEntity(id);
        String mimeType = sysFile.getFileType();
        if (mimeType == null || !mimeType.startsWith("audio/")) {
            throw new BusinessException("当前文件不是音频，不能用于语音智能识别");
        }
        // Qwen OpenAI-compatible audio input expects a URL or a Base64 Data URL, not raw Base64.
        return "data:" + mimeType + ";base64," + readAudioAsBase64(id);
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
    public String readSpreadsheetAsText(Long id) {
        SysFile sysFile = getEntity(id);
        validateSpreadsheetFile(sysFile);
        File file = resolveLocalFile(sysFile);
        String fileName = sysFile.getFileName() == null ? "" : sysFile.getFileName().toLowerCase();
        try {
            if (fileName.endsWith(".csv")) {
                return readCsvAsText(file);
            }
            return readExcelAsText(file);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("解析表格文件失败：" + e.getMessage());
        }
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

    private void validateSpreadsheetFile(SysFile sysFile) {
        String fileName = sysFile.getFileName() == null ? "" : sysFile.getFileName().toLowerCase();
        if (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls") && !fileName.endsWith(".csv")) {
            throw new BusinessException("只支持 xlsx/xls/csv 表格文件");
        }
    }

    private String readCsvAsText(File file) throws Exception {
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        StringBuilder text = new StringBuilder("表格内容（CSV，最多读取前50行）：\n");
        int limit = Math.min(lines.size(), 50);
        for (int i = 0; i < limit; i++) {
            text.append("第").append(i + 1).append("行：").append(lines.get(i)).append('\n');
        }
        appendTruncatedNotice(text, lines.size(), limit);
        return text.toString();
    }

    private String readExcelAsText(File file) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            DataFormatter formatter = new DataFormatter();
            StringBuilder text = new StringBuilder("表格内容（Excel，最多读取前3个Sheet、每个Sheet前50行）：\n");
            int sheetLimit = Math.min(workbook.getNumberOfSheets(), 3);
            for (int sheetIndex = 0; sheetIndex < sheetLimit; sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                text.append("\nSheet：").append(sheet.getSheetName()).append('\n');
                int lastRow = Math.min(sheet.getLastRowNum(), 49);
                for (int rowIndex = 0; rowIndex <= lastRow; rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) {
                        continue;
                    }
                    String rowText = formatRow(row, formatter);
                    if (!rowText.isEmpty()) {
                        text.append("第").append(rowIndex + 1).append("行：").append(rowText).append('\n');
                    }
                }
                if (sheet.getLastRowNum() >= 50) {
                    text.append("（该Sheet剩余行已截断）\n");
                }
            }
            if (workbook.getNumberOfSheets() > sheetLimit) {
                text.append("（剩余Sheet已截断）\n");
            }
            return text.toString();
        }
    }

    private String formatRow(Row row, DataFormatter formatter) {
        return java.util.stream.StreamSupport.stream(row.spliterator(), false)
                .map(cell -> formatter.formatCellValue(cell))
                .filter(value -> value != null && !value.trim().isEmpty())
                .collect(Collectors.joining(" | "));
    }

    private void appendTruncatedNotice(StringBuilder text, int total, int limit) {
        if (total > limit) {
            text.append("（剩余").append(total - limit).append("行已截断）\n");
        }
    }

    private FileVO toVO(SysFile sysFile) {
        FileVO vo = new FileVO();
        BeanUtils.copyProperties(sysFile, vo);
        return vo;
    }
}
