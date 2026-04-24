package com.graduation.crm.modules.system.service.impl;

import com.graduation.crm.modules.system.mapper.SysDeptMapper;
import com.graduation.crm.modules.system.service.DeptService;
import com.graduation.crm.modules.system.vo.DeptVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<DeptVO> tree() {
        List<DeptVO> list = sysDeptMapper.selectDeptList();
        Map<Long, DeptVO> nodeMap = list.stream().collect(Collectors.toMap(DeptVO::getId, item -> item));
        // 部门树用于管理端团队选择器，parentId 为 0 的节点作为根节点。
        list.forEach(item -> {
            DeptVO parent = nodeMap.get(item.getParentId());
            if (parent != null) {
                parent.getChildren().add(item);
            }
        });
        return list.stream()
                .filter(item -> item.getParentId() == null || item.getParentId() == 0)
                .collect(Collectors.toList());
    }
}

