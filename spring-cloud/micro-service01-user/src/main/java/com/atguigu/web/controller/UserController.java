package com.atguigu.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class UserController {
    @Value("${app.app.slogan:默认}")
    private String slogan;

    @Value("${app.app.max-users:0}")
    private int maxUsers;

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id) {
        return "user-" + id + " @ user-service";
    }

    @GetMapping("/slogan")
    public String getSlogan() {
        return slogan + " " + maxUsers;
    }
}
