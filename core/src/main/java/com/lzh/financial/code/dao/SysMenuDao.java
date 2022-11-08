package com.lzh.financial.code.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.lzh.financial.code.domain.entity.SysMenu;

/**
 * 菜单权限表(SysMenu)表数据库访问层
 *
 * @author makejava
 * @since 2022-10-19 22:14:29
 */
@Mapper
public interface SysMenuDao extends BaseMapper<SysMenu> {



    List<String> queryPermsByUserId(Long id);

    List<SysMenu> queryAllMenu();

    List<SysMenu> queryMenuTreeByUserId(Long userId);
}

