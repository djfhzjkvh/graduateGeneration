package com.graduation.crm.modules.project.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProjectSaveDTO {

    @NotBlank(message = "楼盘名称不能为空")
    private String projectName;
    private String city;
    private String region;
    private String address;
    private BigDecimal avgPrice;
    private String highlights;
    private String discountInfo;
    private LocalDate handoverDate;
    private Integer status;
}

