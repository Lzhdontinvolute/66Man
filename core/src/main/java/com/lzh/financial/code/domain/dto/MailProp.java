package com.lzh.financial.code.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailProp implements Serializable {
    private String to;
    private String subject;
    private String text;
}
