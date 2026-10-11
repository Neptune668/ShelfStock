package com.atguigu.cake.controller;

import com.atguigu.cake.common.result.PageResult;
import com.atguigu.cake.common.result.Result;
import com.atguigu.cake.service.ProductService;
import com.atguigu.cake.vo.SaleRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 销售记录查询接口
 */
@Tag(name = "货架（成品）模块")
@RestController
@RequestMapping("/api/sale")
public class SaleController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "销售记录查询")
    @GetMapping("/page")
    public Result<PageResult<SaleRecordVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String saleType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return Result.ok(productService.pageSale(pageNum, pageSize, saleType, startDate, endDate));
    }
}
