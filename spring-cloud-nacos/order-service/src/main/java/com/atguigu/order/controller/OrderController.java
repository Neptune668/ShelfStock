package com.atguigu.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/order/{id}")
    public String getUser(@PathVariable Long id) {
        return "user-" + id + " @ order-service";
    }

    @GetMapping("/order/info/{id}")
    public String call(@PathVariable Long id){
        String url = "http://localhost:8001/user/1";
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }

    @GetMapping("/order/a/{id}")
    public String callAble(@PathVariable Long id) {
        List<ServiceInstance> instances = discoveryClient.getInstances("user-service");
        ServiceInstance ins = instances.get(0);
        String url = "http://" + ins.getHost() + ":" + ins.getPort()
                + "/user/" + id;
        String s = restTemplate.getForObject(url, String.class);
        return s;
    }

}
