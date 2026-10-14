package com.atguigu.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id) {
        return "user-" + id + " @ user-service(16000)";
    }
}
