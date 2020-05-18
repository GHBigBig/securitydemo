package com.zjg.securitydemo.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短信验证码对象SmsCode
 *
 * @author zjg
 * @create 2020-03-13 15:37
 */
@Data
@AllArgsConstructor
@ToString
public class SmsCode implements Serializable {
    private static final long serialVersionUID = 6685785726154272996L;
    private String code;
    private LocalDateTime expireTime;

    public SmsCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    /**
     * 用于判断短信验证码是否已过期
     *
     * @return
     */
    public boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
