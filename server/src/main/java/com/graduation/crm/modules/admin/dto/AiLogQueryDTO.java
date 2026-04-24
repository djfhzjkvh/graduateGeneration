package com.graduation.crm.modules.admin.dto;

import lombok.Data;

@Data
public class AiLogQueryDTO {

    private String bizType;
    private String status;
    private String dateStart;
    private String dateEnd;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

