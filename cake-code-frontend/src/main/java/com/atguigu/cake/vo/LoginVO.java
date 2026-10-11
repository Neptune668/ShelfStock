package com.atguigu.cake.vo;

import lombok.Data;

/**
 * 登录返回结果
 */
@Data
public class LoginVO {

    private String token;

    private Long adminId;

    private String username;

    private String realName;
}
