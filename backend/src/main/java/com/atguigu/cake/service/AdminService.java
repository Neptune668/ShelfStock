package com.atguigu.cake.service;

import com.atguigu.cake.dto.LoginDTO;
import com.atguigu.cake.entity.Admin;
import com.atguigu.cake.vo.AdminInfoVO;
import com.atguigu.cake.vo.LoginVO;
import com.baomidou.mybatisplus.spring.service.IService;

/**
* @author YuanYi
* @description 针对表【admin(管理员表)】的数据库操作Service
* @createDate 2026-10-11 15:49:23
*/
public interface AdminService extends IService<Admin> {

    /**
     * 登录：校验用户名密码并签发 Token
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取当前登录管理员信息
     */
    AdminInfoVO getCurrentAdminInfo();
}
