package com.atguigu.cake.service.impl;

import com.atguigu.cake.common.enums.OutboundReason;
import com.atguigu.cake.common.util.DateUtil;
import com.atguigu.cake.mapper.DashboardMapper;
import com.atguigu.cake.service.DashboardService;
import com.atguigu.cake.vo.ExpiryDistributionVO;
import com.atguigu.cake.vo.OutboundReasonVO;
import com.atguigu.cake.vo.OverviewVO;
import com.atguigu.cake.vo.SaleTrendVO;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据看板业务实现
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public OverviewVO overview() {
        Date today = DateUtil.startOfToday();
        Date tomorrow = DateUtil.addDays(today, 1);

        OverviewVO vo = new OverviewVO();
        vo.setMaterialTotalQuantity(dashboardMapper.sumMaterialQuantity());
        vo.setMaterialTypeCount(dashboardMapper.countMaterial());
        vo.setTodayInboundQuantity(dashboardMapper.sumInboundQuantity(today, tomorrow));
        vo.setTodayOutboundQuantity(dashboardMapper.sumOutboundQuantity(today, tomorrow));
        vo.setOnSaleQuantity(dashboardMapper.sumOnSaleQuantity());
        vo.setProductTypeCount(dashboardMapper.countProduct());
        vo.setTodaySaleCount(dashboardMapper.countSale(today, tomorrow));
        vo.setTodaySaleAmount(dashboardMapper.sumSaleAmount(today, tomorrow));
        vo.setNearExpiryMaterialBatch(dashboardMapper.countNearExpiryMaterialBatch(today));
        vo.setNearExpiryProduct(dashboardMapper.countNearExpiryProduct(today));
        Long expiredPending = dashboardMapper.countExpiredMaterialBatch(today) + dashboardMapper.countExpiredProduct(today);
        vo.setExpiredPending(expiredPending);
        return vo;
    }

    @Override
    public List<SaleTrendVO> saleTrend(Integer days) {
        Date today = DateUtil.startOfToday();
        // 近 N 天：从 (today - days + 1) 到今天
        Date start = DateUtil.addDays(today, -(days - 1));
        Date end = DateUtil.addDays(today, 1);
        return dashboardMapper.listSaleTrend(start, end);
    }

    @Override
    public List<OutboundReasonVO> outboundReason() {
        List<OutboundReasonVO> list = dashboardMapper.listOutboundReason();
        for (OutboundReasonVO vo : list) {
            vo.setReasonName(convertReasonName(vo.getReason()));
        }
        return list;
    }

    @Override
    public List<ExpiryDistributionVO> expiryDistribution() {
        Date today = DateUtil.startOfToday();

        ExpiryDistributionVO material = new ExpiryDistributionVO();
        material.setType("MATERIAL");
        material.setTypeName("原材料");
        material.setCount(dashboardMapper.countNearExpiryMaterialBatch(today));

        ExpiryDistributionVO product = new ExpiryDistributionVO();
        product.setType("PRODUCT");
        product.setTypeName("成品");
        product.setCount(dashboardMapper.countNearExpiryProduct(today));

        return List.of(material, product);
    }

    private String convertReasonName(String reason) {
        if (reason == null) {
            return "";
        }
        try {
            OutboundReason outboundReason = OutboundReason.valueOf(reason);
            return switch (outboundReason) {
                case NORMAL_CONSUME -> "正常烘焙消耗";
                case EXPIRED_DISCARD -> "过期丢弃";
            };
        } catch (IllegalArgumentException e) {
            return reason;
        }
    }
}
