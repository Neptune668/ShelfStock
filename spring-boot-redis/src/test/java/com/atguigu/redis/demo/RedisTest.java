package com.atguigu.redis.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisTest {

    @Autowired
    private StringRedisTemplate redis;   // key/value 都是 String

    @Test
    void stringOps() {
        // 写
        redis.opsForValue().set("hello:redis", "world");
        // 读
        String v = redis.opsForValue().get("hello:redis");
        System.out.println("value = " + v);
    }
}