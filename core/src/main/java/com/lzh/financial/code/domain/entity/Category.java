package com.lzh.financial.code.domain.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 分类表(Category)表实体类
 *
 * @author makejava
 * @since 2022-10-20 16:57:02
 */
@SuppressWarnings("serial")
@Data
@Accessors(chain = true)
public class Category extends Model<Category> {
    
    private Integer id;

    private Long uid;
    //分类名
    private String name;
    //父分类id，如果没有父分类为-1
    private Long pid;
    //描述
    private String description;
    //状态0:正常,1禁用
    private String status;
    
    private Long createBy;
    
    private Long updateBy;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private String delFlag;

    @TableField(exist = false)
    private List<Tag> children;

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    }

