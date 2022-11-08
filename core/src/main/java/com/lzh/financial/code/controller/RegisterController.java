package com.lzh.financial.code.controller;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.UserRegisterDto;
import com.lzh.financial.code.domain.vo.UserInfoVo;
import com.lzh.financial.code.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Api(tags = "注册")
public class RegisterController {
    @Autowired
    private UserService userService;
    @PostMapping("/user/register")
    public ResponseResult register(@RequestBody UserRegisterDto userRegisterDto){
        return userService.register(userRegisterDto);
    }
}
