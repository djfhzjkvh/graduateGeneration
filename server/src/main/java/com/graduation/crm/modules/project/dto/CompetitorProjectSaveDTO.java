package com.graduation.crm.modules.project.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CompetitorProjectSaveDTO {

    @NotBlank(message = "竞品楼盘名称不能为空")
    private String projectName;
    private String region;
    private BigDecimal avgPrice;
    private String discountInfo;
    private String houseTypes;
    private LocalDate handoverDate;
    private String highlights;
    private String weakness;
    private Integer status;
}

