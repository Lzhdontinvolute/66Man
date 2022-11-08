package com.lzh.financial.code.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.financial.code.constants.SystemConstants;
import com.lzh.financial.code.dao.SysMenuDao;
import com.lzh.financial.code.domain.entity.SysMenu;
import com.lzh.financial.code.service.SysMenuService;
import com.lzh.financial.code.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(SysMenu)表服务实现类
 *
 * @author makejava
 * @since 2022-10-19 22:14:29
 */
@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenu> implements SysMenuService {

    @Autowired
    private RedisCache redisCache;

    @Override
    public List<String> queryPermsById(Long uid) {
        //如果用户id为1，则表示是超级管理员，直接返回所有权限
//        if (uid == 1L) {
            //先查缓存，如果缓存中存在，就不访问数据库
            if(ObjectUtil.isEmpty(redisCache.getCacheList("menu:"+uid))){
                //缓存不存在，从数据库获取，并存入缓存
                LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>();
                wrapper.in(SysMenu::getMenuType, SystemConstants.MENU, SystemConstants.BUTTON);
                wrapper.eq(SysMenu::getStatus, SystemConstants.MENU_STATUS_NORMAL);
                List<SysMenu> menusList = list(wrapper);
                List<String> perms = menusList.stream()
                        .map(SysMenu::getPerms)
                        .collect(Collectors.toList());
                redisCache.setCacheList("menu:"+uid,perms);
                return perms;
            }
            //从缓存获取
            List<String> perms = redisCache.getCacheList("menu:" + uid);
            return perms;
//        }
//        return baseMapper.queryPermsByUserId(uid);
    }

    @Override
    public List<SysMenu> queryMenuTreeByUserId(Long userId) {
        //如果是管理员
        List<SysMenu> menus = null;
//        if (userId == 1){
            menus =  baseMapper.queryAllMenu();
//        }else{
//            menus =  baseMapper.queryMenuTreeByUserId(userId);
//        }
        List<SysMenu> menuTree = getMenuTreeByUserId(menus,0L);

        return menuTree;
    }

    private List<SysMenu> getMenuTreeByUserId(List<SysMenu> menus, long l) {
        List<SysMenu> menuTree = menus.stream()
                .filter(m -> m.getParentId().equals(l))
                .map(menu -> menu.setChildren(getChildren(menus, menu)))
                .collect(Collectors.toList());
        return menuTree;
    }


    public List<SysMenu> getChildren(List<SysMenu> menuList, SysMenu menu){
        List<SysMenu> list = menuList.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(menuList, m)))
                .collect(Collectors.toList());
        return list;
    }
}
