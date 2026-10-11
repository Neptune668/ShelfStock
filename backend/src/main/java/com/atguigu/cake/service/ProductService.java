package com.atguigu.cake.service;

import com.atguigu.cake.common.result.PageResult;
import com.atguigu.cake.dto.OffShelfDTO;
import com.atguigu.cake.dto.OnShelfDTO;
import com.atguigu.cake.dto.ProductDTO;
import com.atguigu.cake.dto.SaleDTO;
import com.atguigu.cake.entity.Product;
import com.atguigu.cake.vo.ProductBatchVO;
import com.atguigu.cake.vo.ProductVO;
import com.atguigu.cake.vo.SaleRecordVO;
import com.baomidou.mybatisplus.spring.service.IService;
import java.util.Date;

/**
 * 成品模块业务接口：基础信息 + 货架 + 销售
 */
public interface ProductService extends IService<Product> {

    PageResult<ProductVO> pageProduct(Integer pageNum, Integer pageSize, String name, String category);

    void addProduct(ProductDTO dto);

    void updateProduct(Long id, ProductDTO dto);

    void deleteProduct(Long id);

    PageResult<ProductBatchVO> pageProductBatch(Integer pageNum, Integer pageSize, Long productId, String status);

    void onShelf(OnShelfDTO dto);

    void offShelf(OffShelfDTO dto);

    void sale(SaleDTO dto);

    PageResult<SaleRecordVO> pageSale(Integer pageNum, Integer pageSize, String saleType, Date startDate, Date endDate);
}
