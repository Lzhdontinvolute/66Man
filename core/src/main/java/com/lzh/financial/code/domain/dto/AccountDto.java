package com.lzh.financial.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

//    private String username;
    private Integer id;

    private String name;
    //额度
    private BigDecimal amount;
    private BigDecimal threshold;
    private Integer warn;
    //类型名
    private Integer typeId;

    private String remark;
}
