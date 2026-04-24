package com.graduation.crm.modules.system.dto;

import lombok.Data;

@Data
public class UserQueryDTO {

    private String keyword;
    private Long roleId;
    private Long deptId;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

