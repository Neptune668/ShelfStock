package com.atguigu.cake.controller;

import com.atguigu.cake.common.result.ListData;
import com.atguigu.cake.common.result.Result;
import com.atguigu.cake.service.DashboardService;
import com.atguigu.cake.vo.ExpiryDistributionVO;
import com.atguigu.cake.vo.OutboundReasonVO;
import com.atguigu.cake.vo.OverviewVO;
import com.atguigu.cake.vo.SaleTrendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据看板接口
 */
@Tag(name = "数据看板模块")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "看板总览")
    @GetMapping("/overview")
    public Result<OverviewVO> overview() {
        return Result.ok(dashboardService.overview());
    }

    @Operation(summary = "销售趋势")
    @GetMapping("/sale-trend")
    public Result<ListData<SaleTrendVO>> saleTrend(@RequestParam(defaultValue = "7") Integer days) {
        return Result.ok(ListData.of(dashboardService.saleTrend(days)));
    }

    @Operation(summary = "出库原因占比")
    @GetMapping("/outbound-reason")
    public Result<ListData<OutboundReasonVO>> outboundReason() {
        return Result.ok(ListData.of(dashboardService.outboundReason()));
    }

    @Operation(summary = "临期分布")
    @GetMapping("/expiry-distribution")
    public Result<ListData<ExpiryDistributionVO>> expiryDistribution() {
        return Result.ok(ListData.of(dashboardService.expiryDistribution()));
    }
}
