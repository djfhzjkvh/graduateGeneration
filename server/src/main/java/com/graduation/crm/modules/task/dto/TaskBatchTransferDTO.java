package com.graduation.crm.modules.task.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TaskBatchTransferDTO {
    @NotEmpty(message = "任务ID不能为空")
    private List<Long> taskIds;
    @NotNull(message = "接收人不能为空")
    private Long toUserId;
    private Boolean syncCustomerAdvisor;
    private String reason;
    private Long createdBy;
}
