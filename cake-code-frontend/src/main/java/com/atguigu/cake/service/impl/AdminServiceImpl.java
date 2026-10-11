package com.atguigu.cake.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.cake.entity.Admin;
import com.atguigu.cake.service.AdminService;
import com.atguigu.cake.mapper.AdminMapper;
import org.springframework.stereotype.Service;

/**
* @author YuanYi
* @description 针对表【admin(管理员表)】的数据库操作Service实现
* @createDate 2026-10-11 15:49:23
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService{

}




