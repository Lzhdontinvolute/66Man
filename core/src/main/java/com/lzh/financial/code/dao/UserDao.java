package com.lzh.financial.code.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzh.financial.code.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * (User)表数据库访问层
 *
 * @author makejava
 * @since 2022-10-04 20:25:38
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

}

