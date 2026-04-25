package com.graduation.crm.modules.follow.dto;

import lombok.Data;

@Data
public class NextFollowTimeParseDTO {
    private Long customerId;
    private String content;
    private String baseTime;
}
