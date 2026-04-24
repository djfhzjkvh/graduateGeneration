package com.graduation.crm.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduation.crm.modules.project.dto.ProjectQueryDTO;
import com.graduation.crm.modules.project.entity.CompetitorProject;
import com.graduation.crm.modules.project.vo.CompetitorProjectVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CompetitorProjectMapper extends BaseMapper<CompetitorProject> {

    IPage<CompetitorProjectVO> selectCompetitorPage(IPage<CompetitorProjectVO> page, @Param("query") ProjectQueryDTO query);
}

