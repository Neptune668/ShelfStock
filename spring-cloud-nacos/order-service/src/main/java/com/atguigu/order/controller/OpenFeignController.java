package com.atguigu.order.controller;

import com.atguigu.order.entity.User;
import com.atguigu.order.romate_api.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OpenFeignController {
    @Autowired
    private UserClient userClient;
    @GetMapping("/order/user/{id}")
    public User getOrderUser(@PathVariable Long id) {
        log.info("发送请求前调用日志");
        // 像调用本地方法一样调用远程服务，无需手写 HTTP
        return userClient.getUser(id);
    }
}
