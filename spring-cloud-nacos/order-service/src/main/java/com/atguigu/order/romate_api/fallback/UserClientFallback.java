package com.atguigu.order.romate_api.fallback;

import com.atguigu.order.entity.User;
import com.atguigu.order.romate_api.UserClient;

import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public User getUser(Long id) {
        return new User(id, "默认用户", "服务降级中");
    }
}
