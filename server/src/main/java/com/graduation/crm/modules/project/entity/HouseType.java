package com.graduation.crm.modules.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("crm_house_type")
public class HouseType {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private String typeName;
    private BigDecimal area;
    private String rooms;
    private BigDecimal totalPriceMin;
    private BigDecimal totalPriceMax;
    private String sellingPoints;
    private Integer status;
    private LocalDateTime createdAt;
}

