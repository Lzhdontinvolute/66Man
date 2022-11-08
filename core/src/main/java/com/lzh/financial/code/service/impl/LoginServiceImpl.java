package com.lzh.financial.code.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lzh.financial.code.domain.LoginUser;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.UserLoginDto;
import com.lzh.financial.code.domain.vo.LoginUserVo;
import com.lzh.financial.code.domain.vo.UserInfoVo;
import com.lzh.financial.code.service.LoginService;
import com.lzh.financial.code.utils.JwtUtil;
import com.lzh.financial.code.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import static com.lzh.financial.code.enums.AppHttpCodeEnum.SYSTEM_ERROR;
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(UserLoginDto userLoginDto) {
        //TODO Kaptcha图片验证码验证
//        String password = passwordEncoder.encode(userLoginDto.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (!authenticate.isAuthenticated()){
            return ResponseResult.errorResult(SYSTEM_ERROR,"认证失败");
        }
        //获取loginUser对象
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        //获取userid用来生成token
        Long uid = loginUser.getUser().getUid();
        String token = JwtUtil.createJWT(uid.toString());
        //loginUser存入redis缓存，token返回给前端
        redisCache.setCacheObject("login:"+uid,loginUser);
//        redisCache.setCacheObject("login:"+uid,loginUser,30, TimeUnit.MINUTES);
        HashMap<String, String> map = new HashMap<>();
        map.put("token",token);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult logout() {

        //获取token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //解析token来获取userid
        Long userid = loginUser.getUser().getUid();
        //删除redis中的记录
        redisCache.deleteObject("login:"+userid);
        return ResponseResult.okResult();
    }


}
