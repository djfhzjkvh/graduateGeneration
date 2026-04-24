package com.graduation.crm.modules.admin.dto;

import lombok.Data;

@Data
public class OperLogQueryDTO {

    private Long userId;
    private String bizType;
    private String action;
    private String dateStart;
    private String dateEnd;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

