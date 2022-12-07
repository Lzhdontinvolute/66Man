package com.lzh.financial.code.service;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.ResetDto;
import com.lzh.financial.code.domain.dto.UserLoginDto;

public interface LoginService {
    ResponseResult login(UserLoginDto userLoginDto);

    ResponseResult logout();

    ResponseResult phoneVerify(String phoneNum,String username);

    ResponseResult emailVerify(String email,String username);

    ResponseResult verify(String code,String username);

    ResponseResult resetPassword(ResetDto resetDto);
}
