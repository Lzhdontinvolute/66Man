package com.lzh.financial.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

//    private String username;
    private Integer id;

    private String name;
    //额度
    private BigDecimal amount;
    private BigDecimal lastAmount;

    private BigDecimal threshold;
    //账单日
    private Integer billingDay;

    //还款日
    private Integer repaymentDay;

    private Integer warn;
    //类型名
    private Integer typeId;

    private String remark;
}
