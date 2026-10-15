package com.atguigu.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class ConfigController {
    @Value("${app.slogan}")
    private String configInfo;
    @GetMapping("/user/config")
    public String configScope(){
        return configInfo;
    }
}