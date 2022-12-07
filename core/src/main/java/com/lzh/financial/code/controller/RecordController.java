package com.lzh.financial.code.controller;

import cn.hutool.core.util.ObjectUtil;
import com.lzh.financial.code.dao.AccountDao;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.BillDto;
import com.lzh.financial.code.domain.dto.UpdateBillDto;
import com.lzh.financial.code.enums.AppHttpCodeEnum;
import com.lzh.financial.code.exception.SystemException;
import com.lzh.financial.code.service.BillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/record")
@Api(tags = "记账")
public class RecordController {

    @Autowired
    private BillService billService;

    @PostMapping("/addRecord")
    @ApiOperation(value = "添加记账记录")
    public ResponseResult addOneBill(@RequestBody BillDto billDto){
        if (ObjectUtil.isEmpty(billDto)){
            throw new SystemException(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return billService.addBill(billDto);
    }

    @GetMapping("/listAllRecord")
    @ApiOperation("查询全部记录")
    public ResponseResult listAllRecord(Integer pageNum, Integer pageSize,String recordName){
        return billService.listAllRecord(pageNum, pageSize, recordName);
    }

    @PostMapping("/deleteRecord")
    public ResponseResult deleteRecord(@RequestBody Object ids){
        return billService.deleteRecord(ids);
    }

    @GetMapping("/queryRecordById/{id}")
    public ResponseResult queryRecordById(@PathVariable("id") Integer id){
        return billService.queryRecordById(id);
    }

    @PutMapping("/updateRecord")
    public  ResponseResult updateRecord(@RequestBody UpdateBillDto billDto){
        return billService.updateRecord(billDto);
    }

    @GetMapping("getCurMonthOutcome")
    @ApiOperation(value = "获取当月支出账单数据")
    public ResponseResult getCurMonthOutcome(){
        return billService.getCurMonthOutcome();
    }

    @GetMapping("getCurMonthRecord")
    @ApiOperation(value = "获取当月的账单数据")
    public ResponseResult getCurMonthRecord(){
        return billService.getCurMonthRecord();
    }

    @GetMapping("getMonthRecord/{date}")
    @ApiOperation(value = "按月获取账单数据")
    public ResponseResult getMonthRecord(@PathVariable("date")String date){
        return billService.getMonthRecord(date);
    }

    @GetMapping("getRecordByAccount/{aid}")
    @ApiOperation(value = "按账户获取账单记录")
    public ResponseResult getRecordByAccount(@PathVariable("aid")Long aid){
        return billService.getRecordByAccount(aid);
    }
}
