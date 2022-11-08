package com.lzh.financial.code.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.financial.code.constants.SystemConstants;
import com.lzh.financial.code.dao.CategoryDao;
import com.lzh.financial.code.dao.TagDao;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.entity.Category;
import com.lzh.financial.code.domain.entity.Tag;
import com.lzh.financial.code.domain.vo.CategoryAndTagVo;
import com.lzh.financial.code.service.CategoryService;
import com.lzh.financial.code.utils.BeanCopyUtils;
import com.lzh.financial.code.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2022-10-20 16:57:02
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {


    @Autowired
    private TagDao tagDao;


    public ResponseResult queryCategoryAndTag(){
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Category::getUid,userId);
        wrapper.in(Category::getUid,userId,SystemConstants.SYSTEM_DEFAULT);
        wrapper.eq(Category::getDelFlag, SystemConstants.DEL_FLG_NORMAL);
        //获取分类信息
        List<Category> categories = baseMapper.selectList(wrapper);
        //构建分类关系树
        List<Category> categoryTree = getCategoryTree(categories,0L);
        List<CategoryAndTagVo> vo = BeanCopyUtils.copyBeanList(categoryTree, CategoryAndTagVo.class);
        return ResponseResult.okResult(vo);
    }

    private List<Category> getCategoryTree(List<Category> categories, long l) {
        List<Category> collect = categories.stream()
//                .filter(category -> category.getPid().equals(l))
                .map(category -> category.setChildren(getChildren( category.getId())))
                .collect(Collectors.toList());
        return collect;
    }

    private List<Tag> getChildren( Integer pid) {
        //
        Long userId = SecurityUtils.getUserId();
        //查询标签列表
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getDelFlag, SystemConstants.DEL_FLG_NORMAL);
        queryWrapper.in(Tag::getUid, userId,SystemConstants.SYSTEM_DEFAULT);
        queryWrapper.eq(Tag::getCategoryId, pid);
        List<Tag> tags = tagDao.selectList(queryWrapper);
        return tags;
    }
}

