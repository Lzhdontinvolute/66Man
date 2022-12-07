package com.lzh.financial.code.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Value {
    private String time;
    private BigDecimal value;
    private List<Value> valueList;
}
