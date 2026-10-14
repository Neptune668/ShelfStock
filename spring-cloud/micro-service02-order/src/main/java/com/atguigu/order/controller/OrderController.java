package com.atguigu.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RefreshScope
@RestController
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DiscoveryClient discoveryClient;

    public static final String USER_SERVICE_URL = "http://localhost:16000";

    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable Long id) {
        String response = restTemplate.getForObject(USER_SERVICE_URL + "/user/" + id, String.class);
        return "[加上了远程调用] User 服务返回的结果：" + response;
    }

    @Value("${app.port:默认}")
    private String port;

    @GetMapping("/order/id")
    public String test(@RequestParam("id") Long id) {

        List<ServiceInstance> instances = discoveryClient.getInstances("micro-service01");
        ServiceInstance serviceInstance = instances.get(0);
        String host = serviceInstance.getHost();
        System.out.println(port);
//        int port = serviceInstance.getPort();
        String url = "http://" + host + ":" + port + "/user" + "/" + id;
        String response = restTemplate.getForObject(url, String.class);
        return response + port;
    }

}
