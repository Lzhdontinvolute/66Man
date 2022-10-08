package com.lzh.financial.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {
    //用户名
    private String username;
    //用户密码
    private String password;
    //头像
    private String avatar;
    //昵称
    private String nickName;
    //0女 1男
    private Integer sex;
    //邮箱
    private String email;
    //手机号
    private String phoneNumber;
}
