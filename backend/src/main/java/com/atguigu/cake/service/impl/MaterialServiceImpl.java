package com.atguigu.cake.service.impl;

import com.atguigu.cake.common.enums.MaterialStatus;
import com.atguigu.cake.common.enums.OutboundReason;
import com.atguigu.cake.common.exception.BusinessException;
import com.atguigu.cake.common.result.PageResult;
import com.atguigu.cake.common.result.ResultCode;
import com.atguigu.cake.common.util.DateUtil;
import com.atguigu.cake.dto.MaterialDTO;
import com.atguigu.cake.dto.MaterialInboundDTO;
import com.atguigu.cake.dto.MaterialOutboundDTO;
import com.atguigu.cake.entity.Material;
import com.atguigu.cake.entity.MaterialBatch;
import com.atguigu.cake.entity.MaterialInbound;
import com.atguigu.cake.entity.MaterialOutbound;
import com.atguigu.cake.mapper.MaterialBatchMapper;
import com.atguigu.cake.mapper.MaterialInboundMapper;
import com.atguigu.cake.mapper.MaterialMapper;
import com.atguigu.cake.mapper.MaterialOutboundMapper;
import com.atguigu.cake.security.LoginUser;
import com.atguigu.cake.service.MaterialService;
import com.atguigu.cake.vo.MaterialBatchVO;
import com.atguigu.cake.vo.MaterialVO;
import com.atguigu.cake.vo.OutboundVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 原材料模块业务实现
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material>
    implements MaterialService {

    @Autowired
    private MaterialBatchMapper materialBatchMapper;

    @Autowired
    private MaterialInboundMapper materialInboundMapper;

    @Autowired
    private MaterialOutboundMapper materialOutboundMapper;

    @Override
    public PageResult<MaterialVO> pageMaterial(Integer pageNum, Integer pageSize, String name, String category) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Material::getName, name)
                .eq(StringUtils.hasText(category), Material::getCategory, category)
                .orderByDesc(Material::getId);
        Page<Material> materialPage = page(new Page<>(pageNum, pageSize), wrapper);
        List<MaterialVO> voList = materialPage.getRecords().stream()
                .map(this::toMaterialVO)
                .collect(Collectors.toList());
        return buildPageResult(materialPage.getTotal(), voList);
    }

    @Override
    public void addMaterial(MaterialDTO dto) {
        Material material = new Material();
        BeanUtils.copyProperties(dto, material);
        save(material);
    }

    @Override
    public void updateMaterial(Long id, MaterialDTO dto) {
        Material material = getById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "原材料不存在");
        }
        BeanUtils.copyProperties(dto, material);
        material.setId(id);
        updateById(material);
    }

    @Override
    public void deleteMaterial(Long id) {
        Long batchCount = materialBatchMapper.selectCount(
                new LambdaQueryWrapper<MaterialBatch>().eq(MaterialBatch::getMaterialId, id));
        if (batchCount != null && batchCount > 0) {
            throw new BusinessException(ResultCode.STATUS_NOT_ALLOWED.getCode(), "该原材料已存在批次记录，禁止删除");
        }
        removeById(id);
    }

    @Override
    public PageResult<MaterialBatchVO> pageMaterialBatch(Integer pageNum, Integer pageSize, Long materialId, String status, String keyword) {
        LambdaQueryWrapper<MaterialBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(materialId != null, MaterialBatch::getMaterialId, materialId);
        // keyword 按原材料名称模糊查询，先反查原材料 ID
        if (StringUtils.hasText(keyword)) {
            List<Long> materialIds = list(new LambdaQueryWrapper<Material>().like(Material::getName, keyword))
                    .stream().map(Material::getId).collect(Collectors.toList());
            if (materialIds.isEmpty()) {
                return buildPageResult(0L, Collections.emptyList());
            }
            wrapper.in(MaterialBatch::getMaterialId, materialIds);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(MaterialBatch::getStatus, status);
        }
        wrapper.orderByAsc(MaterialBatch::getExpiryDate);
        Page<MaterialBatch> batchPage = materialBatchMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        Map<Long, Material> materialMap = loadMaterialMap(
                batchPage.getRecords().stream().map(MaterialBatch::getMaterialId).distinct().collect(Collectors.toList()));
        List<MaterialBatchVO> voList = batchPage.getRecords().stream()
                .map(batch -> toMaterialBatchVO(batch, materialMap.get(batch.getMaterialId())))
                .collect(Collectors.toList());
        return buildPageResult(batchPage.getTotal(), voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inbound(MaterialInboundDTO dto) {
        Material material = getById(dto.getMaterialId());
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "原材料不存在");
        }
        // 过期日期 = 生产日期 + 保质期天数
        Date expiryDate = DateUtil.addDays(dto.getProductionDate(), material.getShelfLifeDays());

        MaterialBatch batch = new MaterialBatch();
        batch.setMaterialId(dto.getMaterialId());
        batch.setProductionDate(dto.getProductionDate());
        batch.setExpiryDate(expiryDate);
        batch.setNearExpiryDays(dto.getNearExpiryDays());
        batch.setQuantity(dto.getQuantity());
        batch.setStatus(calcMaterialStatus(expiryDate, dto.getNearExpiryDays()));
        materialBatchMapper.insert(batch);

        MaterialInbound inbound = new MaterialInbound();
        inbound.setMaterialId(dto.getMaterialId());
        inbound.setBatchId(batch.getId());
        inbound.setQuantity(dto.getQuantity());
        inbound.setSupplier(dto.getSupplier());
        inbound.setOperator(getCurrentUsername());
        materialInboundMapper.insert(inbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outbound(MaterialOutboundDTO dto) {
        MaterialBatch batch = materialBatchMapper.selectById(dto.getBatchId());
        if (batch == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "批次不存在");
        }
        // 出库数量不得超过剩余数量
        if (batch.getQuantity().compareTo(dto.getQuantity()) < 0) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        OutboundReason reason = parseReason(dto.getReason());
        // 过期判断用实时剩余天数，避免依赖入库时的快照状态
        long remainingDays = DateUtil.daysBetween(new Date(), batch.getExpiryDate());
        boolean expired = remainingDays < 0;
        if (reason == OutboundReason.EXPIRED_DISCARD) {
            if (!expired) {
                throw new BusinessException(ResultCode.REASON_NOT_MATCH);
            }
        } else {
            if (expired) {
                throw new BusinessException(ResultCode.STATUS_NOT_ALLOWED.getCode(), "已过期批次禁止正常出库，请选择过期丢弃");
            }
        }

        batch.setQuantity(batch.getQuantity().subtract(dto.getQuantity()));
        materialBatchMapper.updateById(batch);

        MaterialOutbound outbound = new MaterialOutbound();
        outbound.setMaterialId(batch.getMaterialId());
        outbound.setBatchId(batch.getId());
        outbound.setQuantity(dto.getQuantity());
        outbound.setReason(reason.name());
        outbound.setOperator(getCurrentUsername());
        materialOutboundMapper.insert(outbound);
    }

    @Override
    public PageResult<OutboundVO> pageOutbound(Integer pageNum, Integer pageSize, String reason, Date startDate, Date endDate) {
        LambdaQueryWrapper<MaterialOutbound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(reason), MaterialOutbound::getReason, reason)
                .ge(startDate != null, MaterialOutbound::getCreatedAt, startDate)
                .lt(endDate != null, MaterialOutbound::getCreatedAt, DateUtil.addDays(endDate, 1))
                .orderByDesc(MaterialOutbound::getId);
        Page<MaterialOutbound> outboundPage = materialOutboundMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        Map<Long, Material> materialMap = loadMaterialMap(
                outboundPage.getRecords().stream().map(MaterialOutbound::getMaterialId).distinct().collect(Collectors.toList()));
        List<OutboundVO> voList = outboundPage.getRecords().stream()
                .map(outbound -> toOutboundVO(outbound, materialMap.get(outbound.getMaterialId())))
                .collect(Collectors.toList());
        return buildPageResult(outboundPage.getTotal(), voList);
    }

    // ==================== 私有辅助方法 ====================

    private MaterialVO toMaterialVO(Material material) {
        MaterialVO vo = new MaterialVO();
        BeanUtils.copyProperties(material, vo);
        return vo;
    }

    private MaterialBatchVO toMaterialBatchVO(MaterialBatch batch, Material material) {
        MaterialBatchVO vo = new MaterialBatchVO();
        vo.setId(batch.getId());
        vo.setMaterialId(batch.getMaterialId());
        if (material != null) {
            vo.setMaterialName(material.getName());
            vo.setUnit(material.getUnit());
        }
        vo.setProductionDate(batch.getProductionDate());
        vo.setExpiryDate(batch.getExpiryDate());
        vo.setNearExpiryDays(batch.getNearExpiryDays());
        vo.setQuantity(batch.getQuantity());
        long remainingDays = DateUtil.daysBetween(new Date(), batch.getExpiryDate());
        vo.setRemainingDays(remainingDays);
        // 实时重算状态，避免存储状态因时间流逝而过期
        vo.setStatus(calcMaterialStatus(batch.getExpiryDate(), batch.getNearExpiryDays()));
        return vo;
    }

    private OutboundVO toOutboundVO(MaterialOutbound outbound, Material material) {
        OutboundVO vo = new OutboundVO();
        vo.setId(outbound.getId());
        if (material != null) {
            vo.setMaterialName(material.getName());
        }
        vo.setBatchId(outbound.getBatchId());
        vo.setQuantity(outbound.getQuantity());
        vo.setReason(outbound.getReason());
        vo.setOperator(outbound.getOperator());
        vo.setCreatedAt(outbound.getCreatedAt());
        return vo;
    }

    private Map<Long, Material> loadMaterialMap(List<Long> materialIds) {
        if (materialIds == null || materialIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return listByIds(materialIds).stream()
                .collect(Collectors.toMap(Material::getId, material -> material));
    }

    private String calcMaterialStatus(Date expiryDate, Integer nearExpiryDays) {
        long remainingDays = DateUtil.daysBetween(new Date(), expiryDate);
        if (remainingDays < 0) {
            return MaterialStatus.EXPIRED.name();
        }
        if (remainingDays <= nearExpiryDays) {
            return MaterialStatus.NEAR_EXPIRY.name();
        }
        return MaterialStatus.IN_STOCK.name();
    }

    private OutboundReason parseReason(String reason) {
        try {
            return OutboundReason.valueOf(reason);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "非法的出库原因：" + reason);
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser.getUsername();
        }
        return null;
    }

    private <T> PageResult<T> buildPageResult(Long total, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setList(list);
        return result;
    }
}
