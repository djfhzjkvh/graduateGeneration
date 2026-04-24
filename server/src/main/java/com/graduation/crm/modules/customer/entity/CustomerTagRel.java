package com.graduation.crm.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_customer_tag_rel")
public class CustomerTagRel {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long customerId;
    private Long tagId;
    private LocalDateTime createdAt;
}

