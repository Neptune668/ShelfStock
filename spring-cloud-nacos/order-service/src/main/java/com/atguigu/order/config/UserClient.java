package com.atguigu.order.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 声明：调用名为 user-service 的服务
@FeignClient(name = "user-service", contextId = "user-service-config")
public interface UserClient {
    // 对应 user-service 的 GET /user/{id}
    @GetMapping("/user/{id}")
    String getUser(@PathVariable("id") Long id);
}