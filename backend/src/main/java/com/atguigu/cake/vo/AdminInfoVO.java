package com.atguigu.cake.vo;

import lombok.Data;

/**
 * 当前登录管理员信息
 */
@Data
public class AdminInfoVO {

    private Long adminId;

    private String username;

    private String realName;
}
