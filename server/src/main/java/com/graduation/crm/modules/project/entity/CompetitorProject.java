package com.graduation.crm.modules.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("crm_competitor_project")
public class CompetitorProject {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectName;
    private String region;
    private BigDecimal avgPrice;
    private String discountInfo;
    private String houseTypes;
    private LocalDate handoverDate;
    private String highlights;
    private String weakness;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

