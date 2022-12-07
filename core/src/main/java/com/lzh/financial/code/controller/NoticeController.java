package com.lzh.financial.code.controller;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.service.AccountService;
import com.lzh.financial.code.service.NoticeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "通知控制")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private AccountService accountService;

    @RequestMapping("getNewNotice")
    public ResponseResult getNewNotice(){

        return noticeService.getNewNotice();
    }

    @GetMapping("listNotice")
    public ResponseResult listNotice(){
        return noticeService.listNotice();
    }

    @RequestMapping("sendNotice")
    public ResponseResult notice(){
        //TODO 发出通知
        return accountService.needNotified();

    }
}
