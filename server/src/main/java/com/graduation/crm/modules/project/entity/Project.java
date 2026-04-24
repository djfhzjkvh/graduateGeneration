package com.graduation.crm.modules.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("crm_project")
public class Project {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectName;
    private String city;
    private String region;
    private String address;
    private BigDecimal avgPrice;
    private String highlights;
    private String discountInfo;
    private LocalDate handoverDate;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

