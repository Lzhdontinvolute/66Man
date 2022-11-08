package com.lzh.financial.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.entity.Category;

/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2022-10-20 16:57:02
 */
public interface CategoryService extends IService<Category> {


    ResponseResult queryCategoryAndTag();
}

