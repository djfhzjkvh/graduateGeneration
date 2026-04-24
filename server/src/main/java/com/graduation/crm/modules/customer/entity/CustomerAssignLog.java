package com.graduation.crm.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户分配日志，对应 crm_customer_assign_log 表。
 */
@Data
@TableName("crm_customer_assign_log")
public class CustomerAssignLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Long fromUserId;
    private Long toUserId;
    private String actionType;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
}
