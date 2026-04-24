package com.graduation.crm.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.crm.modules.system.entity.SysRole;
import com.graduation.crm.modules.system.mapper.SysRoleMapper;
import com.graduation.crm.modules.system.service.RoleService;
import com.graduation.crm.modules.system.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper sysRoleMapper;

    @Override
    public List<RoleVO> listEnabled() {
        // 角色当前只做下拉选择和登录身份识别，权限细分后续再扩展。
        return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, 1)
                        .orderByAsc(SysRole::getId))
                .stream()
                .map(role -> {
                    RoleVO vo = new RoleVO();
                    BeanUtils.copyProperties(role, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }
}

