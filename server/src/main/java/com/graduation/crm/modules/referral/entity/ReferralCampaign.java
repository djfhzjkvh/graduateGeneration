package com.graduation.crm.modules.referral.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("crm_referral_campaign")
public class ReferralCampaign {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String campaignName;
    private String rewardPolicy;
    private String rewardType;
    private BigDecimal rewardAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
