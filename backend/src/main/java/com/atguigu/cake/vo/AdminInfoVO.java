package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 当前登录管理员信息
 */
@Data
@Schema(description = "当前登录管理员信息")
public class AdminInfoVO {

    @Schema(description = "管理员ID")
    private Long adminId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;
}
