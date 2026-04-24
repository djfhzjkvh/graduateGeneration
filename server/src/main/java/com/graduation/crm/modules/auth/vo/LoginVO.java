package com.graduation.crm.modules.auth.vo;

import com.graduation.crm.modules.system.vo.UserVO;
import lombok.Data;

@Data
public class LoginVO {

    private String token;
    private UserVO userInfo;
}

