package com.lzh.financial.code.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.lzh.financial.code.domain.entity.Tag;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryAndTagVo {
    private Integer id;

    private Long uid;
    //分类名
    private String name;
    //父分类id，如果没有父分类为-1
    private Long pid;
    //状态0:正常,1禁用
    private String status;

    private String delFlag;

    private List<Tag> children;
}
