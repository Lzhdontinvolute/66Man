package com.lzh.financial.code.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lzh.financial.code.constants.SystemConstants;
import com.lzh.financial.code.dao.UserDao;
import com.lzh.financial.code.domain.LoginUser;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.MailProp;
import com.lzh.financial.code.domain.dto.ResetDto;
import com.lzh.financial.code.domain.dto.UserLoginDto;
import com.lzh.financial.code.domain.entity.User;
import com.lzh.financial.code.domain.vo.LoginUserVo;
import com.lzh.financial.code.domain.vo.UserInfoVo;
import com.lzh.financial.code.enums.AppHttpCodeEnum;
import com.lzh.financial.code.service.LoginService;
import com.lzh.financial.code.utils.JwtUtil;
import com.lzh.financial.code.utils.RedisCache;
import com.lzh.financial.code.utils.RegexUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
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
    private UserDao userDao;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult login(UserLoginDto userLoginDto) {
        //TODO Kaptcha?????????????????????
//        String password = passwordEncoder.encode(userLoginDto.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        if (!authenticate.isAuthenticated()){
            return ResponseResult.errorResult(SYSTEM_ERROR,"????????????");
        }
        //??????loginUser??????
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        //??????userid????????????token
        Long uid = loginUser.getUser().getUid();
        String token = JwtUtil.createJWT(uid.toString());
        //loginUser??????redis?????????token???????????????
        redisCache.setCacheObject("login:"+uid,loginUser);
//        redisCache.setCacheObject("login:"+uid,loginUser,30, TimeUnit.MINUTES);
        HashMap<String, String> map = new HashMap<>();
        map.put("token",token);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult logout() {

        //??????token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //??????token?????????userid
        Long userid = loginUser.getUser().getUid();
        //??????redis????????????
        redisCache.deleteObject("login:"+userid);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult phoneVerify(String phoneNumber,String username) {

        //???????????????
        if(!RegexUtils.isPhoneInvalid(phoneNumber)){
            //???????????????????????????
            return ResponseResult.errorResult(AppHttpCodeEnum.PHONE_NUMBER_INVALID);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDelFlag, SystemConstants.DEL_FLG_NORMAL)
                .eq(User::getUsername,username)
                .eq(User::getPhoneNumber,phoneNumber);
        User user = userDao.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(user)){
            //?????????????????????????????????????????????????????????
            return ResponseResult.errorResult(AppHttpCodeEnum.PHONE_NOT_USER);
        }
        String key = "verify:"+username;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey){
            // ???????????????????????????????????????????????????
            redisTemplate.delete(key);
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().setIfAbsent(key, code, 3l, TimeUnit.MINUTES);
        // TODO ???????????????????????????
        System.out.println("?????????????????????"+code);
        return ResponseResult.okResult(code);
    }

    @Override
    public ResponseResult emailVerify(String email,String username) {
//???????????????
        if(!RegexUtils.isEmailInvalid(email)){
            //???????????????????????????
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_INVALID);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDelFlag, SystemConstants.DEL_FLG_NORMAL)
                .eq(User::getUsername,username)
                .eq(User::getEmail,email);
        User user = userDao.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(user)){
            //??????????????????????????????????????????????????????
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_NOT_USER);
        }
        String key = "verify:"+username;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey){
            // ???????????????????????????????????????????????????
            redisTemplate.delete(key);
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().setIfAbsent(key, code, 3l, TimeUnit.MINUTES);
        System.out.println("?????????????????????"+code);
        String text = "????????????????????????????????????"+code+",\n?????????????????????????????????\n\n ????????????????????????????????????";
        // ????????????60s????????????????????????????????????????????????????????????????????????
        rabbitTemplate.convertAndSend("send.mail",new MailProp(user.getEmail(),"????????????",text));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult verify(String code,String username) {
        // ????????????????????????6????????????
        if (code.length()!=6){
            return ResponseResult.errorResult(AppHttpCodeEnum.VERIFY_ERROR);
        }
        String key = "verify:"+username;
        String target = redisTemplate.opsForValue().get(key);
        if (target.equals(code)){
            redisTemplate.delete(key);
            return ResponseResult.okResult(true);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.VERIFY_ERROR);
    }

    @Override
    public ResponseResult resetPassword(ResetDto resetDto) {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        if (!resetDto.getNewPassword().equals(resetDto.getConfirmPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.CONFIRM_PASSWORD_FAIL);
        }
        //???????????????
        String encodePassword = passwordEncoder.encode(resetDto.getConfirmPassword());
        userUpdateWrapper.eq("username",resetDto.getUsername())
                .eq("del_flag",SystemConstants.DEL_FLG_NORMAL)
                .set("password",encodePassword);
        userDao.update(null,userUpdateWrapper);
        return ResponseResult.okResult();
    }


}
