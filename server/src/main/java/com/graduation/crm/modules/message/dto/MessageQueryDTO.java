package com.graduation.crm.modules.message.dto;

import lombok.Data;

@Data
public class MessageQueryDTO {

    private Long userId;
    private Integer readStatus;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

