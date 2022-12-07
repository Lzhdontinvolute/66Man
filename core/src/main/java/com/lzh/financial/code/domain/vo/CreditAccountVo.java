package com.lzh.financial.code.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class CreditAccountVo {
    private Long id;

    private Integer typeId;

    private String name;

    private String typeName;

    private BigDecimal threshold;

    private Date billingDay;

    private Date repaymentDay;

    private Integer warn;

    private String remark;

    private BigDecimal amount;
}
