package com.atguigu.cake.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.cake.entity.MaterialBatch;
import com.atguigu.cake.service.MaterialBatchService;
import com.atguigu.cake.mapper.MaterialBatchMapper;
import org.springframework.stereotype.Service;

/**
* @author YuanYi
* @description 针对表【material_batch(原材料批次（库存）)】的数据库操作Service实现
* @createDate 2026-10-11 15:49:23
*/
@Service
public class MaterialBatchServiceImpl extends ServiceImpl<MaterialBatchMapper, MaterialBatch>
    implements MaterialBatchService{

}




