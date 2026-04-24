package com.graduation.crm.modules.follow.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FollowCreateDTO {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "跟进方式不能为空")
    private String followType;

    @NotBlank(message = "跟进结果不能为空")
    private String followResult;

    @NotBlank(message = "跟进内容不能为空")
    private String content;

    private String summary;
    private String nextFollowTime;
    private Long userId;
}

