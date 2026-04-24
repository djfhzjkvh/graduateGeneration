package com.graduation.crm.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.system.entity.SysDept;
import com.graduation.crm.modules.system.vo.DeptVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    List<DeptVO> selectDeptList();
}

