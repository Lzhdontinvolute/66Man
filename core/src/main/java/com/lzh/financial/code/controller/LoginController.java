package com.lzh.financial.code.controller;

import com.lzh.financial.code.domain.LoginUser;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.UserLoginDto;
import com.lzh.financial.code.domain.entity.SysMenu;
import com.lzh.financial.code.domain.entity.User;
import com.lzh.financial.code.domain.vo.AdminUserInfoVo;
import com.lzh.financial.code.domain.vo.RoutersVo;
import com.lzh.financial.code.domain.vo.UserInfoVo;
import com.lzh.financial.code.enums.AppHttpCodeEnum;
import com.lzh.financial.code.exception.SystemException;
import com.lzh.financial.code.service.LoginService;
import com.lzh.financial.code.service.RoleService;
import com.lzh.financial.code.service.SysMenuService;
import com.lzh.financial.code.utils.BeanCopyUtils;
import com.lzh.financial.code.utils.SecurityUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
@Api(tags = "登录")
public class LoginController {

//    @Autowired
//    private UserService userService;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private LoginService loginService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody UserLoginDto userLoginDto){
        if(!StringUtils.hasText(userLoginDto.getUsername())){
            //提示 必须要传用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(userLoginDto);
    }

    @PostMapping("user/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }


    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo(){
        //获取用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //获取登录用户的权限信息
        List<String> perms = menuService.queryPermsById(loginUser.getUser().getUid());
        //获取登录用户的角色信息
        List<String> roleKeysList = roleService.queryRoleKeyByUserId(loginUser.getUser().getUid());
        //返回封装信息
        User user = loginUser.getUser();
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(perms,roleKeysList,userInfoVo);
        return ResponseResult.okResult(adminUserInfoVo);
    }

    @GetMapping("getRouters")
    public ResponseResult<RoutersVo> getRouter(){
        //获取用户信息
        Long userId = SecurityUtils.getUserId();
        //
        List<SysMenu> menus = menuService.queryMenuTreeByUserId(userId);
        return ResponseResult.okResult(new RoutersVo(menus));
    }
}
