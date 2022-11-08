package com.lzh.financial.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.financial.code.domain.entity.SysMenu;

import java.util.List;

/**
 * 菜单权限表(SysMenu)表服务接口
 *
 * @author makejava
 * @since 2022-10-19 22:14:29
 */
public interface SysMenuService extends IService<SysMenu> {

    List<String> queryPermsById(Long uid);

    List<SysMenu> queryMenuTreeByUserId(Long userId);
}

