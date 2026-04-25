package com.graduation.crm.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("follow_rule_config")
public class FollowRuleConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleName;
    private String customerStatus;
    private String intentLevel;
    private Integer intervalDays;
    private String dayOffsets;
    private Integer holidayDelayDays;
    private Integer sortOrder;
    private Integer enabled;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
