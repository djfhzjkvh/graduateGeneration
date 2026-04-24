package com.graduation.crm.modules.project.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.crm.common.exception.BusinessException;
import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.modules.project.dto.CompetitorProjectSaveDTO;
import com.graduation.crm.modules.project.dto.HouseTypeSaveDTO;
import com.graduation.crm.modules.project.dto.ProjectQueryDTO;
import com.graduation.crm.modules.project.dto.ProjectSaveDTO;
import com.graduation.crm.modules.project.entity.CompetitorProject;
import com.graduation.crm.modules.project.entity.HouseType;
import com.graduation.crm.modules.project.entity.Project;
import com.graduation.crm.modules.project.mapper.CompetitorProjectMapper;
import com.graduation.crm.modules.project.mapper.HouseTypeMapper;
import com.graduation.crm.modules.project.mapper.ProjectMapper;
import com.graduation.crm.modules.project.service.ProjectService;
import com.graduation.crm.modules.project.vo.CompetitorProjectVO;
import com.graduation.crm.modules.project.vo.HouseTypeVO;
import com.graduation.crm.modules.project.vo.ProjectVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectMapper projectMapper;
    private final HouseTypeMapper houseTypeMapper;
    private final CompetitorProjectMapper competitorProjectMapper;

    @Override
    public PageResult<ProjectVO> pageProjects(ProjectQueryDTO queryDTO) {
        // 楼盘列表用于管理端资料维护，也会成为 AI 话术中的项目卖点来源。
        Page<ProjectVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<ProjectVO> result = projectMapper.selectProjectPage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public ProjectVO projectDetail(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("楼盘不存在");
        }
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        return vo;
    }

    @Override
    public Long createProject(ProjectSaveDTO dto) {
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        project.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        projectMapper.insert(project);
        return project.getId();
    }

    @Override
    public void updateProject(Long id, ProjectSaveDTO dto) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("楼盘不存在");
        }
        BeanUtils.copyProperties(dto, project);
        projectMapper.updateById(project);
    }

    @Override
    public void deleteProject(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("楼盘不存在");
        }
        // 楼盘删除采用停用，避免历史客户和话术记录引用的资料消失。
        project.setStatus(0);
        projectMapper.updateById(project);
    }

    @Override
    public List<HouseTypeVO> listHouseTypes(Long projectId) {
        return houseTypeMapper.selectByProjectId(projectId);
    }

    @Override
    public Long createHouseType(HouseTypeSaveDTO dto) {
        HouseType houseType = new HouseType();
        BeanUtils.copyProperties(dto, houseType);
        houseType.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        houseTypeMapper.insert(houseType);
        return houseType.getId();
    }

    @Override
    public void updateHouseType(Long id, HouseTypeSaveDTO dto) {
        HouseType houseType = houseTypeMapper.selectById(id);
        if (houseType == null) {
            throw new BusinessException("户型不存在");
        }
        BeanUtils.copyProperties(dto, houseType);
        houseTypeMapper.updateById(houseType);
    }

    @Override
    public void deleteHouseType(Long id) {
        HouseType houseType = houseTypeMapper.selectById(id);
        if (houseType == null) {
            throw new BusinessException("户型不存在");
        }
        houseType.setStatus(0);
        houseTypeMapper.updateById(houseType);
    }

    @Override
    public PageResult<CompetitorProjectVO> pageCompetitors(ProjectQueryDTO queryDTO) {
        // 竞品资料用于管理端维护，也为后续“一句话反击模板”提供数据基础。
        Page<CompetitorProjectVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        IPage<CompetitorProjectVO> result = competitorProjectMapper.selectCompetitorPage(page, queryDTO);
        return PageResult.of(result.getRecords(), result.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public CompetitorProjectVO competitorDetail(Long id) {
        CompetitorProject competitor = competitorProjectMapper.selectById(id);
        if (competitor == null) {
            throw new BusinessException("竞品楼盘不存在");
        }
        CompetitorProjectVO vo = new CompetitorProjectVO();
        BeanUtils.copyProperties(competitor, vo);
        return vo;
    }

    @Override
    public Long createCompetitor(CompetitorProjectSaveDTO dto) {
        CompetitorProject competitor = new CompetitorProject();
        BeanUtils.copyProperties(dto, competitor);
        competitor.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        competitorProjectMapper.insert(competitor);
        return competitor.getId();
    }

    @Override
    public void updateCompetitor(Long id, CompetitorProjectSaveDTO dto) {
        CompetitorProject competitor = competitorProjectMapper.selectById(id);
        if (competitor == null) {
            throw new BusinessException("竞品楼盘不存在");
        }
        BeanUtils.copyProperties(dto, competitor);
        competitorProjectMapper.updateById(competitor);
    }

    @Override
    public void deleteCompetitor(Long id) {
        CompetitorProject competitor = competitorProjectMapper.selectById(id);
        if (competitor == null) {
            throw new BusinessException("竞品楼盘不存在");
        }
        competitor.setStatus(0);
        competitorProjectMapper.updateById(competitor);
    }
}

