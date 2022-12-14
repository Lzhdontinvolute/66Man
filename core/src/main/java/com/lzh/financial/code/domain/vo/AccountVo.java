package com.lzh.financial.code.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class AccountVo {
    private Long id;

    private Integer typeId;

    private String name;

    private String typeName;

    private BigDecimal threshold;

    //账单日
    private Integer billingDay;

    //还款日
    private Integer repaymentDay;

    private Integer warn;

    private String remark;

    private BigDecimal amount;

    private BigDecimal lastAmount;

    private List<BillVo> children;

}
