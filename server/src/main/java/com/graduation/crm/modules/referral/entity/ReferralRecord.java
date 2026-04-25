package com.graduation.crm.modules.referral.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_referral_record")
public class ReferralRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long campaignId;
    private Long ownerCustomerId;
    private String ownerCustomerName;
    private String inviteCode;
    private Long referredCustomerId;
    private String referredCustomerName;
    private String referredMobile;
    private String status;
    private Long advisorId;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
