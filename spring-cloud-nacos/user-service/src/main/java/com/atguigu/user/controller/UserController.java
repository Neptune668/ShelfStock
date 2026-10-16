package com.atguigu.user.controller;

import com.atguigu.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    // 当前实例端口，用于观察负载均衡把请求分发到了哪个实例
    @Value("${server.port}")
    private String port;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return new User(id, "张三", "123456");
    }

    @GetMapping("/user/a{id}")
    public String getA(@PathVariable Long id) {
        // 打印当前实例端口：多次调用时看端口是否变化，即可验证负载均衡
        log.info("user-service 当前端口: {}, 处理请求 /user/{}", port, id);
        return "user-service port: " + port + ", id: " + id;
    }
}
