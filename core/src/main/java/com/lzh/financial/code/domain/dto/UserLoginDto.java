package com.lzh.financial.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {
    //用户名
    private String username;
    //用户密码
    private String password;
    //验证码
    private String captcha;
}
