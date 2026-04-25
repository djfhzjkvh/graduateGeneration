package com.graduation.crm.modules.task.vo;

import lombok.Data;

@Data
public class TaskBatchTransferVO {
    private Integer transferredTaskCount;
    private Integer updatedCustomerCount;
    private Long toUserId;
    private String message;
}
