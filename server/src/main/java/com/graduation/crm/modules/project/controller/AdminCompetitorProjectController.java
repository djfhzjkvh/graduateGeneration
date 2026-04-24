package com.graduation.crm.modules.project.controller;

import com.graduation.crm.common.result.PageResult;
import com.graduation.crm.common.result.Result;
import com.graduation.crm.modules.project.dto.CompetitorProjectSaveDTO;
import com.graduation.crm.modules.project.dto.ProjectQueryDTO;
import com.graduation.crm.modules.project.service.ProjectService;
import com.graduation.crm.modules.project.vo.CompetitorProjectVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理端竞品资料接口。
 *
 * 用于维护竞品均价、优惠、户型和优劣势，为后续竞品对比话术做准备。
 */
@Tag(name = "管理端竞品资料接口")
@RestController
@RequestMapping("/api/admin/competitors")
@RequiredArgsConstructor
public class AdminCompetitorProjectController {

    private final ProjectService projectService;

    /**
     * 分页查询竞品楼盘。
     */
    @GetMapping
    @Operation(summary = "分页查询竞品")
    public Result<PageResult<CompetitorProjectVO>> page(ProjectQueryDTO queryDTO) {
        return Result.success(projectService.pageCompetitors(queryDTO));
    }

    /**
     * 查询竞品楼盘详情。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询竞品详情")
    public Result<CompetitorProjectVO> detail(@PathVariable Long id) {
        return Result.success(projectService.competitorDetail(id));
    }

    /**
     * 新增竞品楼盘。
     */
    @PostMapping
    @Operation(summary = "新增竞品")
    public Result<Long> create(@RequestBody @Valid CompetitorProjectSaveDTO dto) {
        return Result.success(projectService.createCompetitor(dto));
    }

    /**
     * 更新竞品楼盘。
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新竞品")
    public Result<Void> update(@PathVariable Long id, @RequestBody CompetitorProjectSaveDTO dto) {
        projectService.updateCompetitor(id, dto);
        return Result.success();
    }

    /**
     * 停用竞品楼盘。
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除竞品")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.deleteCompetitor(id);
        return Result.success();
    }
}

