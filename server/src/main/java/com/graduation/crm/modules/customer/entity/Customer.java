package com.graduation.crm.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("crm_customer")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerName;
    private String mobile;
    private String gender;
    private Integer age;
    private String source;
    private String status;
    private Long advisorId;
    private Long managerId;
    private Long deptId;
    private String intentLevel;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String region;
    private String houseType;
    private String purpose;
    private String remark;
    private Integer heatScore;
    private LocalDateTime latestFollowTime;
    private LocalDateTime nextFollowTime;
    private Integer deleted;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

