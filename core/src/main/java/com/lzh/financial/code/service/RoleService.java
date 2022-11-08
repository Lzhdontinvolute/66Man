package com.lzh.financial.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.financial.code.domain.entity.Role;

import java.util.List;

/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2022-10-19 22:55:47
 */
public interface RoleService extends IService<Role> {

    List<String> queryRoleKeyByUserId(Long uid);
}

