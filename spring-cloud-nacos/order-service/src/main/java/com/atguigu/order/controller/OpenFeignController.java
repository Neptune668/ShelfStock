package com.atguigu.order.controller;

import com.atguigu.order.config.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenFeignController {
    @Autowired
    private UserClient userClient;
    @GetMapping("/order/user/{id}")
    public String getOrderUser(@PathVariable Long id) {
        // 像调用本地方法一样调用远程服务，无需手写 HTTP
        return userClient.getUser(id);
    }
}
