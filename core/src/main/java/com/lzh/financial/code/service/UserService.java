package com.lzh.financial.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.UserLoginDto;
import com.lzh.financial.code.domain.dto.UserRegisterDto;
import com.lzh.financial.code.domain.entity.User;

/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2022-10-04 20:25:39
 */
public interface UserService extends IService<User> {

    ResponseResult register(UserRegisterDto userRegisterDto);
}

