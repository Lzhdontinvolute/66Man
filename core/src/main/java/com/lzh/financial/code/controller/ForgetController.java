package com.lzh.financial.code.controller;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.ResetDto;
import com.lzh.financial.code.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Api(tags = "忘记密码")
public class ForgetController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/getPhoneVerifyCode")
    @ApiOperation(value = "使用手机验证码")
    public ResponseResult phoneVerify(String phoneNumber,String username){
        return loginService.phoneVerify(phoneNumber,username);
    }
    @GetMapping("/getEmailVerifyCode")
    @ApiOperation(value = "使用邮箱验证码")
    public ResponseResult emailVerify(String email,String username){
        return loginService.emailVerify(email,username);
    }

    @RequestMapping("verify")
    @ApiOperation(value = "校对验证码")
    public ResponseResult verify(String code,String username){
        return loginService.verify(code,username);
    }

    @PostMapping("retrieve")
    @ApiOperation(value = "忘记密码")
    public ResponseResult resetPassword(@RequestBody ResetDto resetDto){
        return loginService.resetPassword(resetDto);
    }
}
