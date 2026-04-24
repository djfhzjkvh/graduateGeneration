package com.graduation.crm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.system.dto.UserCreateDTO;
import com.graduation.crm.modules.system.dto.UserQueryDTO;
import com.graduation.crm.modules.system.dto.UserUpdateDTO;
import com.graduation.crm.modules.system.entity.SysUser;
import com.graduation.crm.modules.system.mapper.SysUserMapper;
import com.graduation.crm.modules.system.service.UserService;
import com.graduation.crm.modules.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 系统用户业务服务实现。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public PageResult<UserVO> page(UserQueryDTO queryDTO) {
        // 用户列表需要展示角色、部门、上级经理等关联信息，因此使用 XML 查询。
        Page<UserVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<UserVO> result = sysUserMapper.selectUserPage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public UserVO detail(Long id) {
        UserVO user = sysUserMapper.selectUserVOById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public SysUser findEnabledByUsername(String username) {
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0)
                .eq(SysUser::getStatus, 1));
    }

    @Override
    public UserVO findUserVOById(Long id) {
        return sysUserMapper.selectUserVOById(id);
    }

    @Override
    public void create(UserCreateDTO dto) {
        SysUser exists = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername())
                .eq(SysUser::getDeleted, 0));
        if (exists != null) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        user.setDeleted(0);
        // 当前阶段使用明文密码配合演示数据；接入正式登录时应替换为 BCrypt。
        sysUserMapper.insert(user);
    }

    @Override
    public void update(Long id, UserUpdateDTO dto) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new BusinessException("用户不存在");
        }
        BeanUtils.copyProperties(dto, user);
        sysUserMapper.updateById(user);
    }

    @Override
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new BusinessException("用户不存在");
        }
        // 用户删除采用逻辑删除，保留历史客户、跟进和任务中的负责人引用。
        user.setDeleted(1);
        sysUserMapper.updateById(user);
    }
}
