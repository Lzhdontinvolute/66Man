package com.lzh.financial.code.domain.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * (Atype)表实体类
 *
 * @author makejava
 * @since 2022-10-20 19:48:38
 */
@SuppressWarnings("serial")
@Data
@Accessors(chain = true)
public class Atype extends Model<Atype> {
    //账户类型id
    @TableId
    private Integer id;

    private Long uid;
    private Long pid;
    //账户类型名
    private String name;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.INSERT)
    private String delFlag;

    @TableField(exist = false)
    private List<Account> children;
    }

