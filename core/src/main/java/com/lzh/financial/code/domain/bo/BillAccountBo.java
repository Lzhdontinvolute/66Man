package com.lzh.financial.code.domain.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
public class BillAccountBo {
    private Long accountId;
    private Long id;
    private BigDecimal amount;
    private Integer categoryId;
    private BigDecimal threshold;
    //收支时间
    private Date recordTime;
}
