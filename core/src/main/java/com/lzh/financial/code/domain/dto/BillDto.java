package com.lzh.financial.code.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDto {
    //账单id
    private Long bid;

//    private String username;
    private Long accountId;
    //标签名
    private Integer tagId;
    //记账类型
    private Integer categoryId;
    //账户类型
    private Integer accountType;
    //数额
    private BigDecimal amount;
    //备注
    private String remark;

    private Date recordTime;

}
