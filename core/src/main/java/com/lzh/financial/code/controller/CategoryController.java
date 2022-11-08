package com.lzh.financial.code.controller;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("content")
@Api(tags = "分类")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/listAllCategory")
    @ApiOperation(value = "查询所有分类")
    public ResponseResult listAllCategory(){
        return categoryService.queryCategoryAndTag();
    }
}
