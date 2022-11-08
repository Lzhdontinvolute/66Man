package com.lzh.financial.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.entity.Atype;

/**
 * (Atype)表服务接口
 *
 * @author makejava
 * @since 2022-10-20 19:48:38
 */
public interface AtypeService extends IService<Atype> {

    ResponseResult getAllType();

    ResponseResult listBaseAccountType();

    ResponseResult queryAccountTypeTree();
}

