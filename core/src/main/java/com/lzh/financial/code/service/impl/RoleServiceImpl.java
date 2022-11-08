package com.lzh.financial.code.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.financial.code.dao.RoleDao;
import com.lzh.financial.code.domain.entity.Role;
import com.lzh.financial.code.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-10-19 22:55:47
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleDao, Role> implements RoleService {

    @Override
    public List<String> queryRoleKeyByUserId(Long id) {
//        if (id == 1L){
            ArrayList<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
//        }
//        return getBaseMapper().queryRoleKeyByUserId(id);
    }
}

