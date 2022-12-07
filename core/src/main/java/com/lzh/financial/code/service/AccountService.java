package com.lzh.financial.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.AccountDto;
import com.lzh.financial.code.domain.entity.Account;

/**
 * (Account)表服务接口
 *
 * @author makejava
 * @since 2022-10-09 16:22:10
 */
public interface AccountService extends IService<Account> {

    ResponseResult addAccount(AccountDto accountDto);

    ResponseResult updateAccount(Account account);

    ResponseResult deleteAccount(Object aid);

    ResponseResult listAllAccount(Integer pageNum, Integer pageSize, String accountName);

    ResponseResult queryById(Long aid);

    Account getDefaultAccount();

    ResponseResult needNotified();
}

