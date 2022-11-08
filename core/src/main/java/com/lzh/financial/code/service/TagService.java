package com.lzh.financial.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.TagDto;
import com.lzh.financial.code.domain.entity.Tag;

/**
 * (Tag)表服务接口
 *
 * @author makejava
 * @since 2022-10-20 16:41:24
 */
public interface TagService extends IService<Tag> {

    ResponseResult getAllTag();

    ResponseResult updateTag(TagDto tagDto);

    ResponseResult insertTag(Tag tag);

    ResponseResult getTag(Integer id);

    ResponseResult deleteTag(Object ids);
}

