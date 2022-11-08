package com.lzh.financial.code.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.lzh.financial.code.domain.entity.Role;

/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2022-10-19 22:55:47
 */
@Mapper
public interface RoleDao extends BaseMapper<Role> {



    List<String> queryRoleKeyByUserId(Long id);
}

