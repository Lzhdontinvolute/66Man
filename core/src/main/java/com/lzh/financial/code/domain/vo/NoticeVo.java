package com.lzh.financial.code.domain.vo;

import com.lzh.financial.code.domain.entity.Notice;
import lombok.Data;

import java.util.List;

@Data
public class NoticeVo {
    private List<Notice> newNotices;
    private List<Notice> oldNotices;
}
