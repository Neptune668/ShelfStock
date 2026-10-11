package com.atguigu.cake.service.impl;

import com.atguigu.cake.common.enums.SaleType;
import com.atguigu.cake.common.enums.ShelfStatus;
import com.atguigu.cake.common.exception.BusinessException;
import com.atguigu.cake.common.result.PageResult;
import com.atguigu.cake.common.result.ResultCode;
import com.atguigu.cake.common.util.DateUtil;
import com.atguigu.cake.dto.OffShelfDTO;
import com.atguigu.cake.dto.OnShelfDTO;
import com.atguigu.cake.dto.ProductDTO;
import com.atguigu.cake.dto.SaleDTO;
import com.atguigu.cake.entity.Product;
import com.atguigu.cake.entity.ProductBatch;
import com.atguigu.cake.entity.SaleRecord;
import com.atguigu.cake.mapper.ProductBatchMapper;
import com.atguigu.cake.mapper.ProductMapper;
import com.atguigu.cake.mapper.SaleRecordMapper;
import com.atguigu.cake.service.ProductService;
import com.atguigu.cake.vo.ProductBatchVO;
import com.atguigu.cake.vo.ProductVO;
import com.atguigu.cake.vo.SaleRecordVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 成品模块业务实现
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService {

    @Autowired
    private ProductBatchMapper productBatchMapper;

    @Autowired
    private SaleRecordMapper saleRecordMapper;

    @Override
    public PageResult<ProductVO> pageProduct(Integer pageNum, Integer pageSize, String name, String category) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Product::getName, name)
                .eq(StringUtils.hasText(category), Product::getCategory, category)
                .orderByDesc(Product::getId);
        Page<Product> productPage = page(new Page<>(pageNum, pageSize), wrapper);
        List<ProductVO> voList = productPage.getRecords().stream()
                .map(this::toProductVO)
                .collect(Collectors.toList());
        return buildPageResult(productPage.getTotal(), voList);
    }

    @Override
    public void addProduct(ProductDTO dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        save(product);
    }

    @Override
    public void updateProduct(Long id, ProductDTO dto) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "成品不存在");
        }
        BeanUtils.copyProperties(dto, product);
        product.setId(id);
        updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Long batchCount = productBatchMapper.selectCount(
                new LambdaQueryWrapper<ProductBatch>().eq(ProductBatch::getProductId, id));
        Long saleCount = saleRecordMapper.selectCount(
                new LambdaQueryWrapper<SaleRecord>().eq(SaleRecord::getProductId, id));
        if ((batchCount != null && batchCount > 0) || (saleCount != null && saleCount > 0)) {
            throw new BusinessException(ResultCode.STATUS_NOT_ALLOWED.getCode(), "该成品已存在货架或销售记录，禁止删除");
        }
        removeById(id);
    }

    @Override
    public PageResult<ProductBatchVO> pageProductBatch(Integer pageNum, Integer pageSize, Long productId, String status) {
        LambdaQueryWrapper<ProductBatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(productId != null, ProductBatch::getProductId, productId);
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProductBatch::getStatus, status);
        }
        wrapper.orderByAsc(ProductBatch::getExpiryDate);
        Page<ProductBatch> batchPage = productBatchMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        Map<Long, Product> productMap = loadProductMap(
                batchPage.getRecords().stream().map(ProductBatch::getProductId).distinct().collect(Collectors.toList()));
        List<ProductBatchVO> voList = batchPage.getRecords().stream()
                .map(batch -> toProductBatchVO(batch, productMap.get(batch.getProductId())))
                .collect(Collectors.toList());
        return buildPageResult(batchPage.getTotal(), voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onShelf(OnShelfDTO dto) {
        Product product = getById(dto.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "成品不存在");
        }
        // 过期日期 = 生产日期 + 保质期天数
        Date expiryDate = DateUtil.addDays(dto.getProductionDate(), product.getShelfLifeDays());

        ProductBatch batch = new ProductBatch();
        batch.setProductId(dto.getProductId());
        batch.setProductionDate(dto.getProductionDate());
        batch.setExpiryDate(expiryDate);
        batch.setNearExpiryDays(dto.getNearExpiryDays());
        batch.setQuantity(dto.getQuantity());
        batch.setStatus(calcShelfStatus(expiryDate, dto.getNearExpiryDays()));
        productBatchMapper.insert(batch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offShelf(OffShelfDTO dto) {
        ProductBatch batch = productBatchMapper.selectById(dto.getBatchId());
        if (batch == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "批次不存在");
        }
        if (batch.getQuantity().compareTo(dto.getQuantity()) < 0) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }
        BigDecimal remain = batch.getQuantity().subtract(dto.getQuantity());
        batch.setQuantity(remain);
        // 全部下架后置为「已下架」
        if (remain.compareTo(BigDecimal.ZERO) == 0) {
            batch.setStatus(ShelfStatus.OFF_SHELF.name());
        }
        productBatchMapper.updateById(batch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sale(SaleDTO dto) {
        ProductBatch batch = productBatchMapper.selectById(dto.getBatchId());
        if (batch == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "批次不存在");
        }
        // 已下架 / 过期下架的批次禁止销售
        if (ShelfStatus.OFF_SHELF.name().equals(batch.getStatus())
                || ShelfStatus.EXPIRED_OFF.name().equals(batch.getStatus())) {
            throw new BusinessException(ResultCode.STATUS_NOT_ALLOWED);
        }
        if (batch.getQuantity().compareTo(dto.getQuantity()) < 0) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }

        Product product = getById(batch.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "成品不存在");
        }

        SaleType saleType = parseSaleType(dto.getSaleType());
        // 过期判断用实时剩余天数，避免依赖入库时的快照状态
        long remainingDays = DateUtil.daysBetween(new Date(), batch.getExpiryDate());
        if (remainingDays < 0) {
            throw new BusinessException(ResultCode.EXPIRED_FORBIDDEN);
        }

        BigDecimal actualPrice;
        if (saleType == SaleType.NORMAL_SALE) {
            // 正常销售按原价
            actualPrice = product.getPrice();
        } else {
            // 临期降价：须处于临期，且折扣价 < 原价
            if (remainingDays > batch.getNearExpiryDays()) {
                throw new BusinessException(ResultCode.STATUS_NOT_ALLOWED.getCode(), "该批次未临期，不能按临期降价销售");
            }
            if (dto.getActualPrice() == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "临期降价必须填写实际成交单价");
            }
            if (dto.getActualPrice().compareTo(product.getPrice()) >= 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "临期折扣价必须小于原价");
            }
            actualPrice = dto.getActualPrice();
        }

        batch.setQuantity(batch.getQuantity().subtract(dto.getQuantity()));
        productBatchMapper.updateById(batch);

        SaleRecord record = new SaleRecord();
        record.setProductId(batch.getProductId());
        record.setBatchId(batch.getId());
        record.setQuantity(dto.getQuantity());
        record.setSaleType(saleType.name());
        record.setOriginalPrice(product.getPrice());
        record.setActualPrice(actualPrice);
        saleRecordMapper.insert(record);
    }

    @Override
    public PageResult<SaleRecordVO> pageSale(Integer pageNum, Integer pageSize, String saleType, Date startDate, Date endDate) {
        LambdaQueryWrapper<SaleRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(saleType), SaleRecord::getSaleType, saleType)
                .ge(startDate != null, SaleRecord::getCreatedAt, startDate)
                .orderByDesc(SaleRecord::getId);
        // endDate 为 null 时不能直接调用 addDays（会 NPE），需先判空
        if (endDate != null) {
            wrapper.lt(SaleRecord::getCreatedAt, DateUtil.addDays(endDate, 1));
        }
        Page<SaleRecord> salePage = saleRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        Map<Long, Product> productMap = loadProductMap(
                salePage.getRecords().stream().map(SaleRecord::getProductId).distinct().collect(Collectors.toList()));
        List<SaleRecordVO> voList = salePage.getRecords().stream()
                .map(record -> toSaleRecordVO(record, productMap.get(record.getProductId())))
                .collect(Collectors.toList());
        return buildPageResult(salePage.getTotal(), voList);
    }

    // ==================== 私有辅助方法 ====================

    private ProductVO toProductVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        return vo;
    }

    private ProductBatchVO toProductBatchVO(ProductBatch batch, Product product) {
        ProductBatchVO vo = new ProductBatchVO();
        vo.setId(batch.getId());
        vo.setProductId(batch.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
            vo.setPrice(product.getPrice());
        }
        vo.setProductionDate(batch.getProductionDate());
        vo.setExpiryDate(batch.getExpiryDate());
        vo.setNearExpiryDays(batch.getNearExpiryDays());
        vo.setQuantity(batch.getQuantity());
        long remainingDays = DateUtil.daysBetween(new Date(), batch.getExpiryDate());
        vo.setRemainingDays(remainingDays);
        // 实时重算状态，避免存储状态因时间流逝而过期
        vo.setStatus(calcShelfStatus(batch.getExpiryDate(), batch.getNearExpiryDays()));
        return vo;
    }

    private SaleRecordVO toSaleRecordVO(SaleRecord record, Product product) {
        SaleRecordVO vo = new SaleRecordVO();
        vo.setId(record.getId());
        if (product != null) {
            vo.setProductName(product.getName());
        }
        vo.setQuantity(record.getQuantity());
        vo.setSaleType(record.getSaleType());
        vo.setOriginalPrice(record.getOriginalPrice());
        vo.setActualPrice(record.getActualPrice());
        vo.setCreatedAt(record.getCreatedAt());
        return vo;
    }

    private Map<Long, Product> loadProductMap(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
    }

    private String calcShelfStatus(Date expiryDate, Integer nearExpiryDays) {
        long remainingDays = DateUtil.daysBetween(new Date(), expiryDate);
        if (remainingDays < 0) {
            return ShelfStatus.EXPIRED_OFF.name();
        }
        if (remainingDays <= nearExpiryDays) {
            return ShelfStatus.DISCOUNT.name();
        }
        return ShelfStatus.ON_SALE.name();
    }

    private SaleType parseSaleType(String saleType) {
        try {
            return SaleType.valueOf(saleType);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "非法的销售类型：" + saleType);
        }
    }

    private <T> PageResult<T> buildPageResult(Long total, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setList(list);
        return result;
    }
}
