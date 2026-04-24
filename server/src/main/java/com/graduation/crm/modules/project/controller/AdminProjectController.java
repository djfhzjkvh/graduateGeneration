package com.graduation.crm.modules.project.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.project.dto.HouseTypeSaveDTO;
import com.graduation.crm.modules.project.dto.ProjectQueryDTO;
import com.graduation.crm.modules.project.dto.ProjectSaveDTO;
import com.graduation.crm.modules.project.service.ProjectService;
import com.graduation.crm.modules.project.vo.HouseTypeVO;
import com.graduation.crm.modules.project.vo.ProjectVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理端楼盘资料接口。
 *
 * 用于维护本楼盘基础信息和户型资料，后续 AI 话术生成会读取这些卖点。
 */
@Tag(name = "管理端楼盘资料接口")
@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final ProjectService projectService;

    /**
     * 分页查询楼盘资料，支持名称、区域和状态筛选。
     */
    @GetMapping
    @Operation(summary = "分页查询楼盘")
    public Result<PageResult<ProjectVO>> page(ProjectQueryDTO queryDTO) {
        return Result.success(projectService.pageProjects(queryDTO));
    }

    /**
     * 查询楼盘详情。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询楼盘详情")
    public Result<ProjectVO> detail(@PathVariable Long id) {
        return Result.success(projectService.projectDetail(id));
    }

    /**
     * 新增楼盘资料。
     */
    @PostMapping
    @Operation(summary = "新增楼盘")
    public Result<Long> create(@RequestBody @Valid ProjectSaveDTO dto) {
        return Result.success(projectService.createProject(dto));
    }

    /**
     * 更新楼盘资料。
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新楼盘")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProjectSaveDTO dto) {
        projectService.updateProject(id, dto);
        return Result.success();
    }

    /**
     * 停用楼盘资料。
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除楼盘")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }

    /**
     * 查询楼盘下的户型列表。
     */
    @GetMapping("/{projectId}/house-types")
    @Operation(summary = "查询楼盘户型")
    public Result<List<HouseTypeVO>> listHouseTypes(@PathVariable Long projectId) {
        return Result.success(projectService.listHouseTypes(projectId));
    }

    /**
     * 新增户型资料。
     */
    @PostMapping("/house-types")
    @Operation(summary = "新增户型")
    public Result<Long> createHouseType(@RequestBody @Valid HouseTypeSaveDTO dto) {
        return Result.success(projectService.createHouseType(dto));
    }

    /**
     * 更新户型资料。
     */
    @PutMapping("/house-types/{id}")
    @Operation(summary = "更新户型")
    public Result<Void> updateHouseType(@PathVariable Long id, @RequestBody HouseTypeSaveDTO dto) {
        projectService.updateHouseType(id, dto);
        return Result.success();
    }

    /**
     * 停用户型资料。
     */
    @DeleteMapping("/house-types/{id}")
    @Operation(summary = "删除户型")
    public Result<Void> deleteHouseType(@PathVariable Long id) {
        projectService.deleteHouseType(id);
        return Result.success();
    }
}

