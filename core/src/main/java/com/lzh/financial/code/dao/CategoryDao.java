package com.lzh.financial.code.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.lzh.financial.code.domain.entity.Category;

/**
 * 分类表(Category)表数据库访问层
 *
 * @author makejava
 * @since 2022-10-20 16:57:02
 */
@Mapper
public interface CategoryDao extends BaseMapper<Category> {

    String queryCategoryById(@Param("cid")Integer cid);

}

