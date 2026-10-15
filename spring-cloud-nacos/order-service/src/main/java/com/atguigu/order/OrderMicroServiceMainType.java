package com.atguigu.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OrderMicroServiceMainType {
    public static void main(String[] args) {
        SpringApplication.run(OrderMicroServiceMainType.class, args);
    }
}
