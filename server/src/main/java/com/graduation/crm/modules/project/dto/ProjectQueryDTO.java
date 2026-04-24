package com.graduation.crm.modules.project.dto;

import lombok.Data;

@Data
public class ProjectQueryDTO {

    private String keyword;
    private String region;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

