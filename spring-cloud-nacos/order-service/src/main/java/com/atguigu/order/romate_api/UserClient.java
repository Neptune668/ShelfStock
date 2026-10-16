package com.atguigu.order.romate_api;

import com.atguigu.order.entity.User;
import com.atguigu.order.romate_api.fallback.UserClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", contextId = "user-service-romate",
        fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {
    @GetMapping("/user/{id}")
    User getUser(@PathVariable("id") Long id);
}
