package com.lzh.financial.code.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UpdateBillDto {
    //账单id
    private Long bid;

    //    private String username;
    private Long oldAid;

    private Long aid;
    //标签名
    private Integer tagId;
    //记账类型
    private Integer categoryId;
    //账户类型
    private Integer accountId;
    //数额
    private BigDecimal oldAmount;
    private BigDecimal amount;
    //备注
    private String remark;

    private Date recordTime;
}
