package com.graduation.crm.modules.ops.vo;

import lombok.Data;

@Data
public class OpsRiskTaskGenerateVO {
    private Integer riskCount;
    private Integer createdTaskCount;
    private Integer skippedRiskCount;
}
