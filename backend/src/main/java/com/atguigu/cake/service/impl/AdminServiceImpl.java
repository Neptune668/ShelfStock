package com.atguigu.cake.service.impl;

import com.atguigu.cake.common.exception.BusinessException;
import com.atguigu.cake.common.result.ResultCode;
import com.atguigu.cake.common.util.JwtUtil;
import com.atguigu.cake.dto.LoginDTO;
import com.atguigu.cake.entity.Admin;
import com.atguigu.cake.mapper.AdminMapper;
import com.atguigu.cake.security.LoginUser;
import com.atguigu.cake.service.AdminService;
import com.atguigu.cake.vo.AdminInfoVO;
import com.atguigu.cake.vo.LoginVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
* @author YuanYi
* @description 针对表【admin(管理员表)】的数据库操作Service实现
* @createDate 2026-10-11 15:49:23
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        Admin admin = getOne(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, loginDTO.getUsername()));
        if (admin == null || !passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
        }
        // 勾选「记住我」时签发更长有效期的 Token，实现一段时间内免登录
        boolean rememberMe = Boolean.TRUE.equals(loginDTO.getRememberMe());
        String token = rememberMe
                ? jwtUtil.generateTokenWithRemember(admin.getId(), admin.getUsername())
                : jwtUtil.generateToken(admin.getId(), admin.getUsername());
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setAdminId(admin.getId());
        loginVO.setUsername(admin.getUsername());
        loginVO.setRealName(admin.getRealName());
        return loginVO;
    }

    @Override
    public AdminInfoVO getCurrentAdminInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Admin admin = loginUser.getAdmin();
        AdminInfoVO infoVO = new AdminInfoVO();
        infoVO.setAdminId(admin.getId());
        infoVO.setUsername(admin.getUsername());
        infoVO.setRealName(admin.getRealName());
        return infoVO;
    }
}
