package com.graduation.crm.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduation.crm.modules.project.dto.ProjectQueryDTO;
import com.graduation.crm.modules.project.entity.Project;
import com.graduation.crm.modules.project.vo.ProjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

    IPage<ProjectVO> selectProjectPage(IPage<ProjectVO> page, @Param("query") ProjectQueryDTO query);
}

