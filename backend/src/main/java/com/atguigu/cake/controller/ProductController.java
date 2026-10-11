package com.atguigu.cake.controller;

import com.atguigu.cake.common.result.PageResult;
import com.atguigu.cake.common.result.Result;
import com.atguigu.cake.dto.OffShelfDTO;
import com.atguigu.cake.dto.OnShelfDTO;
import com.atguigu.cake.dto.ProductDTO;
import com.atguigu.cake.dto.SaleDTO;
import com.atguigu.cake.service.ProductService;
import com.atguigu.cake.vo.ProductBatchVO;
import com.atguigu.cake.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 成品模块接口：基础信息 + 货架上下架 + 销售
 */
@Tag(name = "货架（成品）模块")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "分页查询成品")
    @GetMapping("/page")
    public Result<PageResult<ProductVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        return Result.ok(productService.pageProduct(pageNum, pageSize, name, category));
    }

    @Operation(summary = "新增成品")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody ProductDTO dto) {
        productService.addProduct(dto);
        return Result.ok();
    }

    @Operation(summary = "修改成品")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody ProductDTO dto) {
        productService.updateProduct(id, dto);
        return Result.ok();
    }

    @Operation(summary = "删除成品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.ok();
    }

    @Operation(summary = "货架批次分页查询")
    @GetMapping("/batch/page")
    public Result<PageResult<ProductBatchVO>> batchPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String status) {
        return Result.ok(productService.pageProductBatch(pageNum, pageSize, productId, status));
    }

    @Operation(summary = "成品上架")
    @PostMapping("/on-shelf")
    public Result<Void> onShelf(@Validated @RequestBody OnShelfDTO dto) {
        productService.onShelf(dto);
        return Result.ok();
    }

    @Operation(summary = "成品下架")
    @PostMapping("/off-shelf")
    public Result<Void> offShelf(@Validated @RequestBody OffShelfDTO dto) {
        productService.offShelf(dto);
        return Result.ok();
    }

    @Operation(summary = "成品销售")
    @PostMapping("/sale")
    public Result<Void> sale(@Validated @RequestBody SaleDTO dto) {
        productService.sale(dto);
        return Result.ok();
    }
}
