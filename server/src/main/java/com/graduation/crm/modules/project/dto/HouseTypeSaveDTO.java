package com.graduation.crm.modules.project.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class HouseTypeSaveDTO {

    @NotNull(message = "楼盘ID不能为空")
    private Long projectId;

    @NotBlank(message = "户型名称不能为空")
    private String typeName;
    private BigDecimal area;
    private String rooms;
    private BigDecimal totalPriceMin;
    private BigDecimal totalPriceMax;
    private String sellingPoints;
    private Integer status;
}

