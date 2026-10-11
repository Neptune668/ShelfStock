package com.atguigu.cake.service;

import com.atguigu.cake.vo.ExpiryDistributionVO;
import com.atguigu.cake.vo.OutboundReasonVO;
import com.atguigu.cake.vo.OverviewVO;
import com.atguigu.cake.vo.SaleTrendVO;
import java.util.List;

/**
 * 数据看板业务接口
 */
public interface DashboardService {

    OverviewVO overview();

    List<SaleTrendVO> saleTrend(Integer days);

    List<OutboundReasonVO> outboundReason();

    List<ExpiryDistributionVO> expiryDistribution();
}
