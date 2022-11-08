package com.lzh.financial.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.BillDto;
import com.lzh.financial.code.domain.dto.UpdateBillDto;
import com.lzh.financial.code.domain.entity.Bill;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * (Bill)表服务接口
 *
 * @author makejava
 * @since 2022-10-21 15:43:46
 */
public interface BillService extends IService<Bill> {

    ResponseResult addBill(BillDto billDto);

    ResponseResult listAllRecord(Integer pageNum, Integer pageSize,String recordName);

    ResponseResult deleteRecord(Object ids);

    ResponseResult queryRecordById(Integer id);

    ResponseResult updateRecord(UpdateBillDto billDto);
}

