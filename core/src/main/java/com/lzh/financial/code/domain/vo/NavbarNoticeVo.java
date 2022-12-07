package com.lzh.financial.code.domain.vo;

import com.lzh.financial.code.domain.entity.Notice;
import lombok.Data;

import java.util.List;

@Data
public class NavbarNoticeVo {
    private List<Notice> notices;
    private Boolean hasNew;
}
