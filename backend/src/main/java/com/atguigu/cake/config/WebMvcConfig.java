package com.atguigu.cake.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：前端 SPA 路由回退
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 前端使用 Vue Router history 模式，刷新 /login、/material 等子路径时
        // 后端没有对应的静态文件会返回 404，这里统一 forward 到 index.html 交给前端路由处理
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/material").setViewName("forward:/index.html");
        registry.addViewController("/product").setViewName("forward:/index.html");
        registry.addViewController("/dashboard").setViewName("forward:/index.html");
    }
}
