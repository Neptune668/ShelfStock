package com.atguigu.redis.demo;

import com.atguigu.redis.entity.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void hashObject() {
        // 直接存一个 Java 对象（内部转 JSON）
        Student stu = new Student(255, "Joseph", 243545.323);
        redisTemplate.opsForValue().set("student:255", stu);

        Object back = redisTemplate.opsForValue().get("student:255");
        Student stu1 = (Student) back;
        System.out.println(stu1);//转类型可以操作属性
//        stu1.getName();
        System.out.println(back);


    }
}