package com.graduation.crm.modules.competitor.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CompetitorCompareDTO {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotNull(message = "本楼盘ID不能为空")
    private Long projectId;

    @NotNull(message = "竞品楼盘ID不能为空")
    private Long competitorId;

    private String customerConcern;
    private Long createdBy;
}

