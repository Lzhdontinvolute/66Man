package com.lzh.financial.code.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzh.financial.code.domain.entity.Bill;
import com.lzh.financial.code.domain.vo.BillVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (Bill)表数据库访问层
 *
 * @author makejava
 * @since 2022-10-21 15:43:46
 */
@Mapper
public interface BillDao extends BaseMapper<Bill> {

    List<BillVo> listRecord();

}

