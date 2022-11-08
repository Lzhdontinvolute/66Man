package com.lzh.financial.code.controller;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.AccountDto;
import com.lzh.financial.code.domain.entity.Account;
import com.lzh.financial.code.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@Api(tags = "账户",produces = "账户资产操作接口")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/listAllAccount")
    public ResponseResult listAllAccount(Integer pageNum, Integer pageSize,String accountName){
        return accountService.listAllAccount(pageNum,pageSize,accountName);
    }

    @GetMapping("queryById/{id}")
    public ResponseResult queryById(@PathVariable("id")Long id){
        return accountService.queryById(id);
    }

    @PostMapping
    @ApiOperation(value = "添加账户")
    public ResponseResult addAccount(@RequestBody AccountDto accountDto){
        return accountService.addAccount(accountDto);
    }

    @PutMapping
    @ApiOperation(value = "修改账户")
    public ResponseResult updateAccount(@RequestBody Account Account){
        return accountService.updateAccount(Account);
    }

    @PostMapping("/deleteAccount")
    @ApiOperation(value = "删除账户")
    public ResponseResult deleteAccount(@RequestBody Object aid){
        return accountService.deleteAccount(aid);
    }
}
