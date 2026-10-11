package com.atguigu.cake.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.cake.entity.Material;
import com.atguigu.cake.service.MaterialService;
import com.atguigu.cake.mapper.MaterialMapper;
import org.springframework.stereotype.Service;

/**
* @author YuanYi
* @description 针对表【material(原材料基础信息)】的数据库操作Service实现
* @createDate 2026-10-11 15:49:23
*/
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material>
    implements MaterialService{

}




