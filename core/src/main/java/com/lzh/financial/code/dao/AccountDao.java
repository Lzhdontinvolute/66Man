package com.lzh.financial.code.dao;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.lzh.financial.code.domain.entity.Account;

/**
 * (Account)表数据库访问层
 *
 * @author makejava
 * @since 2022-10-09 16:22:09
 */
@Mapper
public interface AccountDao extends BaseMapper<Account> {

    String queryAccountByAid(@Param("aid") Long aid);
    BigDecimal queryAmountByAid(@Param("aid") Long aid);
}

