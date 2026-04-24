package com.graduation.crm.modules.system.service;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.system.dto.UserCreateDTO;
import com.graduation.crm.modules.system.dto.UserQueryDTO;
import com.graduation.crm.modules.system.dto.UserUpdateDTO;
import com.graduation.crm.modules.system.entity.SysUser;
import com.graduation.crm.modules.system.vo.UserVO;

public interface UserService {

    PageResult<UserVO> page(UserQueryDTO queryDTO);

    UserVO detail(Long id);

    SysUser findEnabledByUsername(String username);

    UserVO findUserVOById(Long id);

    void create(UserCreateDTO dto);

    void update(Long id, UserUpdateDTO dto);

    void delete(Long id);
}

