package com.lzh.financial.code.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.lzh.financial.code.domain.entity.Tag;

/**
 * (Tag)表数据库访问层
 *
 * @author makejava
 * @since 2022-10-20 16:41:24
 */
@Mapper
public interface TagDao extends BaseMapper<Tag> {

    String queryTagById(@Param("tid")Integer tid);


}

