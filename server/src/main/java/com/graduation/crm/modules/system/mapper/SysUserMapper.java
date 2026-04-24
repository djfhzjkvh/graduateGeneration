package com.graduation.crm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduation.crm.modules.system.dto.UserQueryDTO;
import com.graduation.crm.modules.system.entity.SysUser;
import com.graduation.crm.modules.system.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    UserVO selectUserVOById(@Param("id") Long id);

    UserVO selectUserVOByUsername(@Param("username") String username);

    IPage<UserVO> selectUserPage(IPage<UserVO> page, @Param("query") UserQueryDTO query);
}

