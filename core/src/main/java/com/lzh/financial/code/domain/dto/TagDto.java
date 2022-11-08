package com.lzh.financial.code.domain.dto;

import lombok.Data;

@Data
public class TagDto {
    private Integer id;
    private String tagName;
    private String remark;
    private Integer status;
}
