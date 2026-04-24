package com.graduation.crm.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.crm.modules.project.entity.HouseType;
import com.graduation.crm.modules.project.vo.HouseTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HouseTypeMapper extends BaseMapper<HouseType> {

    List<HouseTypeVO> selectByProjectId(@Param("projectId") Long projectId);
}

