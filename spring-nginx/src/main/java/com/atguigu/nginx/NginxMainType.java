package com.atguigu.nginx;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class NginxMainType {

    public static void main(String[] args) {
        SpringApplication.run(NginxMainType.class, args);
    }
    @GetMapping("/test/load/balance")
    public String testLoadBalance(HttpServletRequest request) {
        return "Current port:" + request.getServerPort();
    }

}
