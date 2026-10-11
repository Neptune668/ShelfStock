package com.atguigu.cake;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.atguigu.cake.mapper"})
public class CakeMainType {
    public static void main(String[] args) {
        SpringApplication.run(CakeMainType.class, args);
    }

}