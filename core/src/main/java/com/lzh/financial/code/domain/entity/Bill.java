package com.lzh.financial.code.domain.entity;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * (Bill)表实体类
 *
 * @author makejava
 * @since 2022-10-21 15:43:46
 */
@SuppressWarnings("serial")
@Data
public class Bill extends Model<Bill> {
    //账单id
    private Long bid;
    //用户id
    private Long uid;
    //数额
    private BigDecimal amount;
    //余额
    private BigDecimal balance;
    //标签
    private Integer tagId;
    //记账类型
    private Integer categoryId;
//    //账户类型
//    private Integer accountType;
    //账户id
    private Long accountId;
    //收支时间
    private Date recordTime;
    //备注
    private String remark;
    //账单更新时间
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private String delFlag;

}

