package com.graduation.crm.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String mobile;
    private String openid;
    private Long roleId;
    private Long deptId;
    private Long managerId;
    private Integer status;
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

