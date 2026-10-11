package com.atguigu.cake.service;

import com.atguigu.cake.common.result.PageResult;
import com.atguigu.cake.dto.MaterialDTO;
import com.atguigu.cake.dto.MaterialInboundDTO;
import com.atguigu.cake.dto.MaterialOutboundDTO;
import com.atguigu.cake.entity.Material;
import com.atguigu.cake.vo.MaterialBatchVO;
import com.atguigu.cake.vo.MaterialVO;
import com.atguigu.cake.vo.OutboundVO;
import com.baomidou.mybatisplus.spring.service.IService;
import java.util.Date;

/**
 * 原材料模块业务接口：基础信息 + 批次库存 + 入库/出库
 */
public interface MaterialService extends IService<Material> {

    PageResult<MaterialVO> pageMaterial(Integer pageNum, Integer pageSize, String name, String category);

    void addMaterial(MaterialDTO dto);

    void updateMaterial(Long id, MaterialDTO dto);

    void deleteMaterial(Long id);

    PageResult<MaterialBatchVO> pageMaterialBatch(Integer pageNum, Integer pageSize, Long materialId, String status, String keyword);

    void inbound(MaterialInboundDTO dto);

    void outbound(MaterialOutboundDTO dto);

    PageResult<OutboundVO> pageOutbound(Integer pageNum, Integer pageSize, String reason, Date startDate, Date endDate);
}
