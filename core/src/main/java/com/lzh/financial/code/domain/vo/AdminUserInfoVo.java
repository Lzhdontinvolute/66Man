package com.lzh.financial.code.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserInfoVo {
    private List<String> permissions;

    private List<String> roles;

    private UserInfoVo user;
}
