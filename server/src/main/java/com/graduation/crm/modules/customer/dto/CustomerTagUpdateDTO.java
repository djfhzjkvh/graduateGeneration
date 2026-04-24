package com.graduation.crm.modules.customer.dto;

import lombok.Data;

@Data
public class CustomerTagUpdateDTO {

    private String tagName;
    private String tagType;
    private String color;
    private Integer status;
}

