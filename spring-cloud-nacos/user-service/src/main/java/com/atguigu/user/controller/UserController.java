package com.atguigu.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id) {
        return "user-" + id + " @ user-service"+"这是user的方法";
    }
    @GetMapping("/user/a{id}")
    public String getA(@PathVariable Long id){
        return "这个geta方法"+id;
    }

}
