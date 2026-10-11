package com.atguigu.cake.common.exception;

import com.atguigu.cake.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常，抛出后由全局异常处理器统一转换为 Result 返回，不在 catch 中静默吞掉
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
