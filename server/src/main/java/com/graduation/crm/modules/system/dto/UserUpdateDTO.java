package com.graduation.crm.modules.system.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {

    private String password;
    private String nickname;
    private String mobile;
    private Long roleId;
    private Long deptId;
    private Long managerId;
    private Integer status;
}

