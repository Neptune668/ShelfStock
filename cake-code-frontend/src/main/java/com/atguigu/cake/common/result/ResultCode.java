package com.atguigu.cake.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一业务码，禁止在代码中散落裸数字
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    SYSTEM_ERROR(500, "系统异常"),

    STOCK_NOT_ENOUGH(1001, "库存不足"),
    STATUS_NOT_ALLOWED(1002, "批次状态不允许当前操作"),
    REASON_NOT_MATCH(1003, "出库原因与批次状态不匹配"),
    EXPIRED_FORBIDDEN(1004, "成品已过期，禁止销售");

    private final Integer code;

    private final String message;
}
