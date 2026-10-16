package com.atguigu.user.controller;

import com.atguigu.user.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return new User(id, "张三","123456");
    }
    @GetMapping("/user/a{id}")
    public String getA(@PathVariable Long id){
        return "这个geta方法"+id;
    }

}
