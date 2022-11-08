package com.lzh.financial.code.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.financial.code.dao.UserDao;
import com.lzh.financial.code.domain.LoginUser;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.UserLoginDto;
import com.lzh.financial.code.domain.dto.UserRegisterDto;
import com.lzh.financial.code.domain.entity.Account;
import com.lzh.financial.code.domain.entity.User;
import com.lzh.financial.code.domain.vo.LoginUserVo;
import com.lzh.financial.code.domain.vo.UserInfoVo;
import com.lzh.financial.code.enums.AppHttpCodeEnum;
import com.lzh.financial.code.exception.SystemException;
import com.lzh.financial.code.service.AccountService;
import com.lzh.financial.code.service.UserService;
import com.lzh.financial.code.utils.IDMaker;
import com.lzh.financial.code.utils.JwtUtil;
import com.lzh.financial.code.utils.RedisCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static com.lzh.financial.code.enums.AppHttpCodeEnum.SYSTEM_ERROR;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2022-10-04 20:25:39
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountService accountService;

    @Override
    @Transactional
    public ResponseResult register(UserRegisterDto userRegisterDto) {
        User user = BeanUtil.toBean(userRegisterDto, User.class);
        //判断非空
        if (!StringUtils.hasText(user.getUsername())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPhoneNumber())){
            throw new SystemException(AppHttpCodeEnum.PHONE_NUMBER_NOT_NULL);
        }
        //判断用户名，手机号，邮箱的在数据库中是否已存在
        if (!userNameExist(user.getUsername())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (!phoneNumExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.PHONE_NUMBER_EXIST);
        }

        if (!EmailExist(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }

        if (!isValidPhoneNumber(user.getPhoneNumber())){
            throw new SystemException(AppHttpCodeEnum.PHONE_NUMBER_INVALID);
        }
        if (!isValidEmail(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_INVALID);
        }

        //加密后再存入数据库
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Long id = IDMaker.generateId();
        user.setUid(id);
        user.setNickName("用户"+id);
        user.setStatus(1);
        user.setType(1);
        save(user);
        //新用户信息保存后，同时创建系统默认账户
        Account defaultAccount = accountService.getDefaultAccount();
        defaultAccount.setUid(id);
        accountService.save(defaultAccount);
        return ResponseResult.okResult("注册成功");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if ((phoneNumber != null) && (!phoneNumber.isEmpty())) {
            return phoneNumber.matches("^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$");
        }
        return false;
    }
    private boolean isValidEmail(String email) {
        if ((email != null) && (!email.isEmpty())) {
            return Pattern.matches( "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", email);
        }
        return false;
    }

    private boolean EmailExist(String email) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getEmail,email);
        return count(userLambdaQueryWrapper)==0;
    }

    private boolean phoneNumExist(String phoneNum) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhoneNumber,phoneNum);
        return count(userLambdaQueryWrapper)==0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername,userName);
        return count(userLambdaQueryWrapper)==0;
    }
}

