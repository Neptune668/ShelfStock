package com.atguigu.cake.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.cake.entity.ProductBatch;
import com.atguigu.cake.service.ProductBatchService;
import com.atguigu.cake.mapper.ProductBatchMapper;
import org.springframework.stereotype.Service;

/**
* @author YuanYi
* @description 针对表【product_batch(成品批次（货架库存）)】的数据库操作Service实现
* @createDate 2026-10-11 15:49:23
*/
@Service
public class ProductBatchServiceImpl extends ServiceImpl<ProductBatchMapper, ProductBatch>
    implements ProductBatchService{

}




