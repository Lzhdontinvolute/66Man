package com.lzh.financial.code.domain.vo;


import com.lzh.financial.code.domain.entity.SysMenu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutersVo {
    private List<SysMenu> menus;
}
