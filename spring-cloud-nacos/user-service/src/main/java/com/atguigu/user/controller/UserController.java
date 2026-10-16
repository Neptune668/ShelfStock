package com.atguigu.user.controller;

import com.atguigu.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return new User(id, "张三", "123456");
    }

    @GetMapping("/user/a{id}")
    public String getA(@PathVariable Long id) {
        List<ServiceInstance> instances = discoveryClient.getInstances("user-service");
        ServiceInstance ins = instances.get(0);
        String url = "http://" + ins.getHost() + ":" + ins.getPort()
                + "/user/" + id;
        return url;
    }

}
