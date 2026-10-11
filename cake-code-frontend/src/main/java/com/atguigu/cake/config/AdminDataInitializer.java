package com.atguigu.cake.config;

import com.atguigu.cake.entity.Admin;
import com.atguigu.cake.mapper.AdminMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时初始化默认管理员账号（仅当不存在时创建），便于首次登录验证
 */
@Slf4j
@Component
public class AdminDataInitializer implements CommandLineRunner {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String DEFAULT_REAL_NAME = "店长";

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Long count = adminMapper.selectCount(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, DEFAULT_USERNAME));
        if (count != null && count > 0) {
            return;
        }
        Admin admin = new Admin();
        admin.setUsername(DEFAULT_USERNAME);
        admin.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        admin.setRealName(DEFAULT_REAL_NAME);
        adminMapper.insert(admin);
        log.info("已初始化默认管理员账号：{} / {}", DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }
}
