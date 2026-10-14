package com.atguigu.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    public static final String USER_SERVICE_URL = "http://localhost:16000";

    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable Long id) {
        String response = restTemplate.getForObject(USER_SERVICE_URL + "/user/" + id, String.class);
        return "[加上了远程调用] User 服务返回的结果：" + response;
    }
}
