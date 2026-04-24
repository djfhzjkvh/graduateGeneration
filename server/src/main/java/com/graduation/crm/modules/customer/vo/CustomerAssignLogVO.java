package com.graduation.crm.modules.customer.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户分配日志返回对象。
 */
@Data
public class CustomerAssignLogVO {

    private Long id;
    private Long customerId;
    private Long fromUserId;
    private Long toUserId;
    private String actionType;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
}
