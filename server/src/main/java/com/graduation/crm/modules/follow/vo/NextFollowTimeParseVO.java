package com.graduation.crm.modules.follow.vo;

import lombok.Data;

@Data
public class NextFollowTimeParseVO {
    private String nextFollowTime;
    private String timeText;
    private String confidence;
    private String reason;
    private String rawModelOutput;
}
