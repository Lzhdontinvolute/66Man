package com.lzh.financial.code.domain.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * (Account)表实体类
 *
 * @author makejava
 * @since 2022-10-09 16:22:09
 */
@SuppressWarnings("serial")
@Data
public class Account extends Model<Account> {
    //账户id
    @TableId
    private Long id;
    //用户id
    private Long uid;
    
    private String name;
    //额度
    private BigDecimal amount;
    private BigDecimal threshold;
    private Integer warn;
    //账户类型id
    @TableField(value = "type_id")
    private Integer typeId;
    //备注
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String delFlag;


    }

