package com.lzh.financial.code.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.financial.code.constants.SystemConstants;
import com.lzh.financial.code.dao.TagDao;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.TagDto;
import com.lzh.financial.code.domain.entity.Tag;
import com.lzh.financial.code.domain.vo.PageVo;
import com.lzh.financial.code.domain.vo.TagVo;
import com.lzh.financial.code.enums.AppHttpCodeEnum;
import com.lzh.financial.code.service.TagService;
import com.lzh.financial.code.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (Tag)表服务实现类
 *
 * @author makejava
 * @since 2022-10-20 16:41:24
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagDao, Tag> implements TagService {

    @Override
    public ResponseResult getAllTag() {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getDelFlag, SystemConstants.DEL_FLG_NORMAL)
                .in(Tag::getUid,userId,0);
        List<Tag> tags = baseMapper.selectList(queryWrapper);

        Page<Tag> tagVoPage = new Page<>();
        Page<Tag> page = page(tagVoPage, queryWrapper);
        List<Tag> records = page.getRecords();
        List<TagVo> tagVos = BeanUtil.copyToList(records, TagVo.class);
        return ResponseResult.okResult(new PageVo(tagVos,page.getTotal()));
    }

    @Override
    public ResponseResult updateTag(TagDto tagDto) {
        Tag tag = BeanUtil.toBean(tagDto, Tag.class);
        boolean b = updateById(tag);
        if (b){
            return ResponseResult.okResult("更新成功");
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
    }

    @Override
    public ResponseResult insertTag(Tag tag) {
        Long userId = SecurityUtils.getUserId();
        tag.setUid(userId);
        save(tag);
        return ResponseResult.okResult("插入成功");
    }

    @Override
    public ResponseResult getTag(Integer id) {
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(Tag::getDelFlag,SystemConstants.DEL_FLG_NORMAL)
                .in(Tag::getUid,id,0);
        Tag tags = baseMapper.selectOne(tagLambdaQueryWrapper);
        TagVo tagVo = BeanUtil.toBean(tags, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    @Override
    public ResponseResult deleteTag(Object ids) {
        Long[] longs = Convert.toLongArray(ids);
        //逻辑删除
        Tag tag = new Tag();
        tag.setDelFlag(SystemConstants.DEL_FLG_BANNED);
        UpdateWrapper<Tag> tagUpdateWrapper = new UpdateWrapper<>();
        tagUpdateWrapper.eq("del_flag",SystemConstants.DEL_FLG_NORMAL)
                .in("id",longs);
        boolean update = update(tag, tagUpdateWrapper);
        if (update){
            return ResponseResult.okResult("更新成功");
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"更新失败");
    }
}

 