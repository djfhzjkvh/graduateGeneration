package com.graduation.crm.modules.customer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("crm_customer_tag")
public class CustomerTag {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String tagName;
    private String tagType;
    private String color;
    private Integer status;
    private LocalDateTime createdAt;
}

