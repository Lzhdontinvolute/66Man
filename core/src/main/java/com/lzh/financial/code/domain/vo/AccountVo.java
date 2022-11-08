package com.lzh.financial.code.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class AccountVo {
    private Long id;

    private Integer typeId;

    private String name;

    private String typeName;

    private BigDecimal threshold;

    private Integer warn;

    private String remark;

    private BigDecimal amount;
}
