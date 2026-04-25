package com.graduation.crm.modules.lead.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_lead_prompt_rule")
public class LeadPromptRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String sourceType;
    private String errorType;
    private String ruleContent;
    private Integer priority;
    private Integer enabled;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
