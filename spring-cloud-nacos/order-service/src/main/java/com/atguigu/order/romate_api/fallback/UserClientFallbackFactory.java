package com.atguigu.order.romate_api.fallback;

import com.atguigu.order.entity.User;
import com.atguigu.order.romate_api.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public User getUser(Long id) {
                // 可以根据 cause 区分不同异常，记录日志
                log.error("调用 user-service 失败", cause);
                return new User(id, "工厂降级", "服务降级：" + cause.getMessage());
            }
        };
    }
}