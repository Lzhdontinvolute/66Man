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

    @Override
    public ResponseResult phoneVerify(String phoneNumber,String username) {

        //校验手机号
        if(!RegexUtils.isPhoneInvalid(phoneNumber)){
            //返回格式错误细信息
            return ResponseResult.errorResult(AppHttpCodeEnum.PHONE_NUMBER_INVALID);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDelFlag, SystemConstants.DEL_FLG_NORMAL)
                .eq(User::getUsername,username)
                .eq(User::getPhoneNumber,phoneNumber);
        User user = userDao.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(user)){
            //查询结果为空，说明用户名与手机号不对应
            return ResponseResult.errorResult(AppHttpCodeEnum.PHONE_NOT_USER);
        }
        String key = "verify:"+username;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey){
            // 如果缓存中已存在旧的验证码，则删除
            redisTemplate.delete(key);
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().setIfAbsent(key, code, 3l, TimeUnit.MINUTES);
        // TODO 发送验证码到手机中
        System.out.println("手机验证码为："+code);
        return ResponseResult.okResult(code);
    }

    @Override
    public ResponseResult emailVerify(String email,String username) {
//校验手机号
        if(!RegexUtils.isEmailInvalid(email)){
            //返回格式错误细信息
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_INVALID);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getDelFlag, SystemConstants.DEL_FLG_NORMAL)
                .eq(User::getUsername,username)
                .eq(User::getEmail,email);
        User user = userDao.selectOne(queryWrapper);
        if(ObjectUtil.isEmpty(user)){
            //查询结果为空，说明用户名与邮箱不对应
            return ResponseResult.errorResult(AppHttpCodeEnum.EMAIL_NOT_USER);
        }
        String key = "verify:"+username;
        Boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey){
            // 如果缓存中已存在旧的验证码，则删除
            redisTemplate.delete(key);
        }
        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().setIfAbsent(key, code, 3l, TimeUnit.MINUTES);
        System.out.println("邮箱验证码为："+code);
        String text = "本次找回密码的验证码为："+code+",\n请在三分钟内完成验证。\n\n 注：以最新的验证码为准！";
        // 前端限制60s请求间隔，这期间允许重复发送邮件，以最新的为准。
        rabbitTemplate.convertAndSend("send.mail",new MailProp(user.getEmail(),"找回密码",text));
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult verify(String code,String username) {
        // 若验证码长度不是6，那必错
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
        //对密码加密
        String encodePassword = passwordEncoder.encode(resetDto.getConfirmPassword());
        userUpdateWrapper.eq("username",resetDto.getUsername())
                .eq("del_flag",SystemConstants.DEL_FLG_NORMAL)
                .set("password",encodePassword);
        userDao.update(null,userUpdateWrapper);
        return ResponseResult.okResult();
    }


}
