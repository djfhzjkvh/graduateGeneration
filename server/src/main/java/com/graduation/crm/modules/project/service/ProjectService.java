package com.graduation.crm.modules.project.service;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.project.dto.CompetitorProjectSaveDTO;
import com.graduation.crm.modules.project.dto.HouseTypeSaveDTO;
import com.graduation.crm.modules.project.dto.ProjectQueryDTO;
import com.graduation.crm.modules.project.dto.ProjectSaveDTO;
import com.graduation.crm.modules.project.vo.CompetitorProjectVO;
import com.graduation.crm.modules.project.vo.HouseTypeVO;
import com.graduation.crm.modules.project.vo.ProjectVO;

import java.util.List;

public interface ProjectService {

    PageResult<ProjectVO> pageProjects(ProjectQueryDTO queryDTO);

    ProjectVO projectDetail(Long id);

    Long createProject(ProjectSaveDTO dto);

    void updateProject(Long id, ProjectSaveDTO dto);

    void deleteProject(Long id);

    List<HouseTypeVO> listHouseTypes(Long projectId);

    Long createHouseType(HouseTypeSaveDTO dto);

    void updateHouseType(Long id, HouseTypeSaveDTO dto);

    void deleteHouseType(Long id);

    PageResult<CompetitorProjectVO> pageCompetitors(ProjectQueryDTO queryDTO);

    CompetitorProjectVO competitorDetail(Long id);

    Long createCompetitor(CompetitorProjectSaveDTO dto);

    void updateCompetitor(Long id, CompetitorProjectSaveDTO dto);

    void deleteCompetitor(Long id);
}

