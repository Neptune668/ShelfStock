package com.atguigu.cake.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录返回结果
 */
@Data
@Schema(description = "登录返回结果")
public class LoginVO {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "管理员ID")
    private Long adminId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;
}
