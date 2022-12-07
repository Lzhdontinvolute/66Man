package com.lzh.financial.code.domain.vo;

//import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RecordVo implements Serializable {
    private List<BillVo> children;
    private String time;
    private BigDecimal income;
    private BigDecimal outcome;
}
