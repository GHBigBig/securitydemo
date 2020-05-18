package com.zjg.securitydemo.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码类型的异常类
 *
 *
 * @author zjg
 * @create 2020-03-13 12:34
 */
public class ValidateCodeException  extends AuthenticationException {
    private static final long serialVersionUID = -8812074237509865461L;

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
