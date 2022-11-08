package com.lzh.financial.code.service;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.UserLoginDto;

public interface LoginService {
    ResponseResult login(UserLoginDto userLoginDto);

    ResponseResult logout();
}
