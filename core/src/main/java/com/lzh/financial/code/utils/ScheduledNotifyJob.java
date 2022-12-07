package com.lzh.financial.code.utils;

import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
public class ScheduledNotifyJob {

    @Autowired
    private AccountService accountService;

    @Scheduled(cron="0 0 0 * * ? ")
    public void notice(){
        //TODO 发出通知
        accountService.needNotified();

    }

}
