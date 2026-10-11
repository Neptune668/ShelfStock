package com.atguigu.cake.controller;

import com.atguigu.cake.common.result.PageResult;
import com.atguigu.cake.common.result.Result;
import com.atguigu.cake.dto.MaterialDTO;
import com.atguigu.cake.dto.MaterialInboundDTO;
import com.atguigu.cake.dto.MaterialOutboundDTO;
import com.atguigu.cake.service.MaterialService;
import com.atguigu.cake.vo.MaterialBatchVO;
import com.atguigu.cake.vo.MaterialVO;
import com.atguigu.cake.vo.OutboundVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 原材料模块接口：基础信息 + 批次库存 + 入库/出库
 */
@Tag(name = "原材料模块")
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Operation(summary = "分页查询原材料")
    @GetMapping("/page")
    public Result<PageResult<MaterialVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        return Result.ok(materialService.pageMaterial(pageNum, pageSize, name, category));
    }

    @Operation(summary = "新增原材料")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody MaterialDTO dto) {
        materialService.addMaterial(dto);
        return Result.ok();
    }

    @Operation(summary = "修改原材料")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody MaterialDTO dto) {
        materialService.updateMaterial(id, dto);
        return Result.ok();
    }

    @Operation(summary = "删除原材料")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        materialService.deleteMaterial(id);
        return Result.ok();
    }

    @Operation(summary = "批次库存分页查询")
    @GetMapping("/batch/page")
    public Result<PageResult<MaterialBatchVO>> batchPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        return Result.ok(materialService.pageMaterialBatch(pageNum, pageSize, materialId, status, keyword));
    }

    @Operation(summary = "原材料入库")
    @PostMapping("/inbound")
    public Result<Void> inbound(@Validated @RequestBody MaterialInboundDTO dto) {
        materialService.inbound(dto);
        return Result.ok();
    }

    @Operation(summary = "原材料出库")
    @PostMapping("/outbound")
    public Result<Void> outbound(@Validated @RequestBody MaterialOutboundDTO dto) {
        materialService.outbound(dto);
        return Result.ok();
    }

    @Operation(summary = "出库流水查询")
    @GetMapping("/outbound/page")
    public Result<PageResult<OutboundVO>> outboundPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return Result.ok(materialService.pageOutbound(pageNum, pageSize, reason, startDate, endDate));
    }
}
