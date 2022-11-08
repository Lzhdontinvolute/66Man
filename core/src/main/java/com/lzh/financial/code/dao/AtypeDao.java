package com.lzh.financial.code.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.lzh.financial.code.domain.entity.Atype;

/**
 * (Atype)表数据库访问层
 *
 * @author makejava
 * @since 2022-10-20 19:48:38
 */
@Mapper
public interface AtypeDao extends BaseMapper<Atype> {

    String queryTypeNameByTid(Integer id);
}

