package com.atguigu.cake.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.cake.entity.Product;
import com.atguigu.cake.service.ProductService;
import com.atguigu.cake.mapper.ProductMapper;
import org.springframework.stereotype.Service;

/**
* @author YuanYi
* @description 针对表【product(成品基础信息)】的数据库操作Service实现
* @createDate 2026-10-11 15:49:23
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

}




