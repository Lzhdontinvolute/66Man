package com.lzh.financial.code.controller;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.TagDto;
import com.lzh.financial.code.domain.entity.Tag;
import com.lzh.financial.code.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
@Api(tags = "标签管理")
public class TagController {
    @Autowired
    private TagService tagService;

    @GetMapping("/tag/listAllTag")
    @ApiOperation(value = "查询所有标签")
    public ResponseResult getAllTag(){
        return tagService.getAllTag();
    }

    @GetMapping("/tag/{id}")
    @ApiOperation(value = "获取标签")
    public ResponseResult getTagById(@PathVariable("id") Integer id){
        return tagService.getTag(id);
    }

    @PutMapping("tag/updateTag")
    @ApiOperation(value = "更新标签")
    public ResponseResult updateTag(@RequestBody TagDto tagDto){
        return tagService.updateTag(tagDto);
    }

    @PostMapping("tag/insertTag")
    @ApiOperation(value = "插入标签")
    public ResponseResult insertTag(@RequestBody Tag tag){
        return tagService.insertTag(tag);
    }

    @PostMapping("tag/delete")
    public ResponseResult deleteTag(@RequestBody Object ids){
        return tagService.deleteTag(ids);
    }
}
