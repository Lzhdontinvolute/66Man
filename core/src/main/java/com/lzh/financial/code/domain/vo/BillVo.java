package com.lzh.financial.code.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class BillVo implements Serializable {

    //账单id
    private Long bid;
    //数额
    private BigDecimal amount;
    //余额
    private BigDecimal balance;
    //备注
    private String remark;
    //标签
    private String tagName;
    //记账类型
    private String categoryName;
    //账户类型
    private String accountName;
    //标签
    private Integer tagId;
    //记账类型
    private Integer categoryId;
    //账户id
    private Long accountId;
    //收支时间
    private Date recordTime;
}
