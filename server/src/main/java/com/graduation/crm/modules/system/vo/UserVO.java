package com.graduation.crm.modules.system.vo;

import lombok.Data;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String mobile;
    private Long roleId;
    private String roleCode;
    private String roleName;
    private Long deptId;
    private String deptName;
    private Long managerId;
    private String managerName;
    private Integer status;
}

