package com.graduation.crm.modules.file.vo;

import lombok.Data;

@Data
public class FileVO {

    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
}

