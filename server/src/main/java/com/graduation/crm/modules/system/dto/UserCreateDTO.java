package com.graduation.crm.modules.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserCreateDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String mobile;

    @NotNull(message = "角色不能为空")
    private Long roleId;

    private Long deptId;
    private Long managerId;
    private Integer status;
}

