package com.lzh.financial.code.domain.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (Tag)表实体类
 *
 * @author makejava
 * @since 2022-10-20 16:41:24
 */
@SuppressWarnings("serial")
@Data
public class Tag extends Model<Tag> {
    //类型id
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String tagName;

    @TableField(value = "category_id")
    private Integer categoryId;

    private Long uid;

    private String remark;


    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private String delFlag;


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

