package com.atguigu.cake.controller;

import com.atguigu.cake.common.result.Result;
import com.atguigu.cake.dto.LoginDTO;
import com.atguigu.cake.service.AdminService;
import com.atguigu.cake.vo.AdminInfoVO;
import com.atguigu.cake.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员认证接口，仅负责接收请求与返回结果，业务逻辑在 AdminService
 */
@Tag(name = "认证模块")
@RestController
@RequestMapping("/api/auth")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        return Result.ok(adminService.login(loginDTO));
    }

    @Operation(summary = "登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT 无状态：真正登出由前端清除本地 Token 实现，服务端仅返回成功
        return Result.ok();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<AdminInfoVO> info() {
        return Result.ok(adminService.getCurrentAdminInfo());
    }
}
