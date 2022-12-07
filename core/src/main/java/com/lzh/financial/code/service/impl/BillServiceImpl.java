package com.lzh.financial.code.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.CalendarUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONConverter;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.lzh.financial.code.constants.SystemConstants;
import com.lzh.financial.code.dao.*;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.bo.BillAccountBo;
import com.lzh.financial.code.domain.dto.BillDto;
import com.lzh.financial.code.domain.dto.UpdateBillDto;
import com.lzh.financial.code.domain.entity.Account;
import com.lzh.financial.code.domain.entity.Bill;
import com.lzh.financial.code.domain.vo.BillVo;
import com.lzh.financial.code.domain.vo.PageVo;
import com.lzh.financial.code.domain.Value;
import com.lzh.financial.code.domain.vo.RecordVo;
import com.lzh.financial.code.enums.AppHttpCodeEnum;
import com.lzh.financial.code.exception.SystemException;
import com.lzh.financial.code.service.BillService;
import com.lzh.financial.code.utils.BeanCopyUtils;
import com.lzh.financial.code.utils.IDMaker;
import com.lzh.financial.code.utils.MapKeyComparator;
import com.lzh.financial.code.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.lzh.financial.code.enums.AppHttpCodeEnum.DELETE_FAIL;
import static com.lzh.financial.code.enums.AppHttpCodeEnum.UPDATE_FAIL;

/**
 * (Bill)表服务实现类
 *
 * @author makejava
 * @since 2022-10-21 15:43:46
 */
@Service("billService")
public class BillServiceImpl extends ServiceImpl<BillDao, Bill> implements BillService {

    @Autowired
    private AccountDao accountDao ;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private TagDao tagDao;

    @Override
    @Transactional
    public ResponseResult addBill(BillDto billDto) {
        // 非空判断
        if(ObjectUtil.isNull(billDto.getCategoryId())){
            throw new SystemException(AppHttpCodeEnum.CATEGORY_NOT_NULL);
        }
        if(ObjectUtil.isNull(billDto.getTagId())){
            throw new SystemException(AppHttpCodeEnum.TAG_NOT_NULL);
        }
        if(ObjectUtil.isNull(billDto.getAccountId())){
            throw new SystemException(AppHttpCodeEnum.ACCOUNT_NOT_NULL);
        }
        if(ObjectUtil.isNull(billDto.getAmount())){
            throw new SystemException(AppHttpCodeEnum.AMOUNT_NOT_NULL);
        }
        if(ObjectUtil.isNull(billDto.getRecordTime())){
            throw new SystemException(AppHttpCodeEnum.RECORD_TIME_NOT_NULL);
        }
        //获取当前登陆用户信息
        Long userId = SecurityUtils.getUserId();
        //转换，保存传入的订单信息
        Bill bill = BeanUtil.toBean(billDto, Bill.class);
        Long id = IDMaker.generateId();
        bill.setBid(id);
        bill.setUid(userId);
        //获取对应账户信息
        Account account = accountDao.selectById(bill.getAccountId());
        //根据操作类型（收入/支出）和金额，同步修改对应账户中的余额
        if (bill.getCategoryId()== SystemConstants.INCOME){
            //如果是收入，那么就将当前传入的金额加到对应账户中
            //将新的收入账单金额添加到对应账户
            account.setAmount(account.getAmount().add(bill.getAmount()));
            //保存本次记账后的对应余额
            bill.setBalance(accountDao.queryAmountByAid(bill.getAccountId()).add(bill.getAmount()));
            //更新账户
            UpdateWrapper<Account> wrapper = new UpdateWrapper<>();
            wrapper.eq("id",bill.getAccountId());
            accountDao.update(account,wrapper);
        }
        else if (bill.getCategoryId()== SystemConstants.OUTCOME){
            //如果是收入，那么就将当前传入的金额加到对应账户中
            //将新的收入账单金额添加到对应账户
            account.setAmount(account.getAmount().subtract(bill.getAmount()));
            //将新的收入账单金额添加到对应账户
            bill.setBalance(accountDao.queryAmountByAid(bill.getAccountId()).subtract(bill.getAmount()));
            //更新账户
            UpdateWrapper<Account> wrapper = new UpdateWrapper<>();
            wrapper.eq("id",bill.getAccountId());
            accountDao.update(account,wrapper);
        }
        save(bill);
        return ResponseResult.okResult("添加成功");
    }


    @Override
    public ResponseResult listAllRecord(Integer pageNum, Integer pageSize,String accountName) {
        //获取用户信息
        Long userId = SecurityUtils.getUserId();
        //查询数据库
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getDelFlag,SystemConstants.DEL_FLG_NORMAL)
//                .eq(ObjectUtil.isNotNull(recordName),Bill::get)
                .eq(Bill::getUid,userId);
        Page<Bill> page;
        if(pageNum!=null||pageSize!=null){
             page = new Page<>(pageNum,pageSize);
        }else{
            page = new Page<>(1,Integer.MAX_VALUE);
        }
        page(page, wrapper);
        List<Bill> records = page.getRecords();
        List<BillVo> billVos = BeanCopyUtils.copyBeanList(records, BillVo.class);
        List<BillVo> vos = billVos.stream()
                //获取账户名
                .map(billVo -> billVo.setAccountName(accountDao.queryAccountByAid(billVo.getAccountId())))
                //获取操作名
                .map(billVo -> billVo.setCategoryName(categoryDao.queryCategoryById(billVo.getCategoryId())))
                //获取标签名
                .map(billVo -> billVo.setTagName(tagDao.queryTagById(billVo.getTagId())))
//                //获取对应余额
//                .map(billVo -> billVo.setCurBalance(accountDao.queryAmountByAid(billVo.getAid()).subtract(billVo.getAmount())))
                .collect(Collectors.toList());
        long total = page.getTotal();
        return ResponseResult.okResult(new PageVo(vos,total));
    }

    @Override
    @Transactional
    public ResponseResult deleteRecord(Object ids) {
        Long userId = SecurityUtils.getUserId();
        Long[] longArray = Convert.toLongArray(ids);
        //逻辑删除
        UpdateWrapper<Bill> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("del_flag",SystemConstants.DEL_FLG_NORMAL)
                .in("bid",longArray)
                .set("del_flag",SystemConstants.DEL_FLG_BANNED);
        boolean update = update(null, updateWrapper);

        //根据ids拿到Bill集合
        LambdaQueryWrapper<Bill> billLambdaQueryWrapper = new LambdaQueryWrapper<>();
        billLambdaQueryWrapper.eq(Bill::getDelFlag,SystemConstants.DEL_FLG_BANNED)
                .eq(Bill::getUid,userId)
                .in(Bill::getBid,longArray);
        List<Bill> bills = baseMapper.selectList(billLambdaQueryWrapper);
        List<BillAccountBo> bos = BeanCopyUtils.copyBeanList(bills, BillAccountBo.class);
        List<BillAccountBo> boList = bos.stream()
                .map(billAccountBo -> billAccountBo.setId(billAccountBo.getAccountId()))
                .collect(Collectors.toList());
//        List<Account> accountList = BeanCopyUtils.copyBeanList(boList, Account.class);

        for(BillAccountBo a:boList){
            UpdateWrapper<Account> accountUpdateWrapper = new UpdateWrapper<>();
            if (a.getCategoryId().equals(SystemConstants.INCOME)){
                Account account = accountDao.selectById(a.getId());
                accountUpdateWrapper.eq("del_flag",SystemConstants.DEL_FLG_NORMAL)
                        .eq("id",a.getId())
                        .set("amount",account.getAmount().subtract(a.getAmount()));
                accountDao.update(null, accountUpdateWrapper);
            }else{
                Account account = accountDao.selectById(a.getId());
                accountUpdateWrapper.eq("del_flag",SystemConstants.DEL_FLG_NORMAL)
                        .eq("id",a.getId())
                        .set("amount",a.getAmount().add(account.getAmount()));
                accountDao.update(null, accountUpdateWrapper);
            }
        }
        if (update){
            return ResponseResult.okResult();
        }
        return ResponseResult.errorResult(DELETE_FAIL,DELETE_FAIL.getMsg());
    }

    @Override
    public ResponseResult queryRecordById(Integer id) {
//        LambdaQueryWrapper<Bill> billLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Bill bill = baseMapper.selectById(id);
        BillVo billVo = BeanCopyUtils.copyBean(bill, BillVo.class);
        return ResponseResult.okResult(billVo);
    }

    @Override
    @Transactional
    public ResponseResult updateRecord(UpdateBillDto billDto) {
        /**
         *更新账单
         *修改账户后，处理账户之间的金额变化
         *修改金额后，处理账户金额以及当前账单对应余额
         */
        //获取修改后的账户编号
        Long oldAid = billDto.getOldAid();
        Long newAid = billDto.getAid();
        //如果新旧账户不一样，就说明修改了账户，那么就要改变账户信息
        if (!oldAid.equals(newAid)){
            if (billDto.getCategoryId().equals(SystemConstants.INCOME)){
                //如果是收入类型，如果改变了账户，那么就更新账户金额信息
                //新账户加上该收入
                Account newAccount = accountDao.selectById(newAid);
                UpdateWrapper<Account> accountUpdateWrapper = new UpdateWrapper<>();
                accountUpdateWrapper.eq("id",newAid)
                        .set("amount",newAccount.getAmount().add(billDto.getAmount()));
                accountDao.update(null,accountUpdateWrapper);
                //旧帐户减去该收入
                Account oldAccount = accountDao.selectById(oldAid);
                UpdateWrapper<Account> oldAccountUpdateWrapper = new UpdateWrapper<>();
                oldAccountUpdateWrapper.eq("id",oldAid)
                        .set("amount",oldAccount.getAmount().subtract(billDto.getOldAmount()));
                accountDao.update(null,oldAccountUpdateWrapper);
            }
            if(billDto.getCategoryId().equals(SystemConstants.OUTCOME)){
                //如果是收入类型，如果改变了账户，那么就更新账户金额信息
                //新帐户减去该支出
                Account newAccount = accountDao.selectById(newAid);
                UpdateWrapper<Account> accountUpdateWrapper = new UpdateWrapper<>();
                accountUpdateWrapper.eq("id",newAid)
                        .set("amount",newAccount.getAmount().subtract(billDto.getAmount()));
                accountDao.update(null,accountUpdateWrapper);
                //旧帐户加回该支出
                Account oldAccount = accountDao.selectById(oldAid);
                UpdateWrapper<Account> oldAccountUpdateWrapper = new UpdateWrapper<>();
                oldAccountUpdateWrapper.eq("id",oldAid)
                        .set("amount",oldAccount.getAmount().add(billDto.getOldAmount()));
                accountDao.update(null,oldAccountUpdateWrapper);
            }
        }else{
            //如果没有更改账户信息，仅仅更改金额或者时间，那就更新对应账户的金额
            if (billDto.getCategoryId().equals(SystemConstants.INCOME)){

                Account account = accountDao.selectById(newAid);
                UpdateWrapper<Account> accountUpdateWrapper = new UpdateWrapper<>();
                BigDecimal oldAmount = billDto.getOldAmount();
                BigDecimal amount = billDto.getAmount();
                if (oldAmount.compareTo(amount)==1){
                    //如果修改金额小于原来的金额，那么更新账单的同时要减去该差值
                    BigDecimal subtract = oldAmount.subtract(amount);
                    accountUpdateWrapper.eq("id",newAid)
                            .set("amount",account.getAmount().subtract(subtract));
                    accountDao.update(null,accountUpdateWrapper);
                }else{
                    //反之把金额修改大，就要在账户上加上该差值
                    BigDecimal subtract = amount.subtract(oldAmount);
                    accountUpdateWrapper.eq("id",newAid)
                            .set("amount",account.getAmount().add(subtract));
                    accountDao.update(null,accountUpdateWrapper);
                }
            }
            if(billDto.getCategoryId().equals(SystemConstants.OUTCOME)){

                Account account = accountDao.selectById(newAid);
                UpdateWrapper<Account> accountUpdateWrapper = new UpdateWrapper<>();
                BigDecimal oldAmount = billDto.getOldAmount();
                BigDecimal amount = billDto.getAmount();
                if (oldAmount.compareTo(amount)==1){
                    //如果修改金额小于原来的金额，那么更新账单的同时要加去该差值
                    BigDecimal subtract = oldAmount.subtract(amount);
                    accountUpdateWrapper.eq("id",newAid)
                            .set("amount",account.getAmount().add(subtract));
                    accountDao.update(null,accountUpdateWrapper);
                }else{
                    //反之把金额修改大，就要在账户上减去该差值
                    BigDecimal subtract = amount.subtract(oldAmount);
                    accountUpdateWrapper.eq("id",newAid)
                            .set("amount",account.getAmount().subtract(subtract));
                    accountDao.update(null,accountUpdateWrapper);
                }
            }
        }

        Bill bill = BeanCopyUtils.copyBean(billDto, Bill.class);
        //更新账单
        boolean update = lambdaUpdate().eq(Bill::getBid, bill.getBid()).update(bill);
//        boolean res = updateById(bill);
        if (update){
            return ResponseResult.okResult();
        }
        return ResponseResult.errorResult(UPDATE_FAIL.getCode(), UPDATE_FAIL.getMsg());
    }

    @Override
    public ResponseResult getCurMonthOutcome() {
        Long userId = SecurityUtils.getUserId();
        HashMap<String, BigDecimal> daysOutcome = new HashMap<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<Value> values = new ArrayList<>();
        // 当前时间信息
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Bill::getUid,userId)
                .eq(Bill::getDelFlag,SystemConstants.DEL_FLG_NORMAL)
                .eq(Bill::getCategoryId,SystemConstants.OUTCOME)
                .apply("MONTH(record_time) = MONTH(NOW())")
                .orderBy(true,true,Bill::getRecordTime);
        List<Bill> bills = baseMapper.selectList(queryWrapper);
        bills.stream()
                .map(bill -> {
                    String timeStr = format.format(bill.getRecordTime());
                    if (daysOutcome.containsKey(timeStr)){
                        daysOutcome.put(timeStr,bill.getAmount().add(daysOutcome.get(timeStr)));
                    }
                    else{
                        daysOutcome.put(timeStr,bill.getAmount());
                    }
                    return daysOutcome;
                })
                .forEach(a-> System.out.println());
        for (Map.Entry<String,BigDecimal> entry : daysOutcome.entrySet()){
            Value value = new Value();
            value.setTime(entry.getKey());
            value.setValue(entry.getValue());
            values.add(value);
        }
        //values按时间排序
        ListUtil.sortByProperty(values,"time");
        return ResponseResult.okResult(values);
    }

    @Override
    public ResponseResult getCurMonthRecord() {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Bill> billLambdaQueryWrapper = new LambdaQueryWrapper<>();
        billLambdaQueryWrapper.eq(Bill::getUid,userId)
                .eq(Bill::getDelFlag,SystemConstants.DEL_FLG_NORMAL)
                .apply("MONTH(record_time) = MONTH(NOW())");
        List<Bill> bills = list(billLambdaQueryWrapper);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<BillVo> billVos = BeanCopyUtils.copyBeanList(bills, BillVo.class);
        List<BillVo> vos = billVos.stream()
                //获取账户名
                .map(billVo -> billVo.setAccountName(accountDao.queryAccountByAid(billVo.getAccountId())))
                //获取操作名
                .map(billVo -> billVo.setCategoryName(categoryDao.queryCategoryById(billVo.getCategoryId())))
                //获取标签名
                .map(billVo -> billVo.setTagName(tagDao.queryTagById(billVo.getTagId())))
//                //获取对应余额
//                .map(billVo -> billVo.setCurBalance(accountDao.queryAmountByAid(billVo.getAid()).subtract(billVo.getAmount())))
                .collect(Collectors.toList());
        ListUtil.sortByProperty(vos,"recordTime");
        HashMap<String, List<BillVo>> billMap = new HashMap<>();
//        vos.stream()
//                .map(billVo -> {
//                    String timeStr = format.format(billVo.getRecordTime());
//                    if(CollUtil.isEmpty(billMap.get(timeStr))){
//                        List<BillVo> tempList = new ArrayList<>();
//                        tempList.add(billVo);
//                        billMap.put(timeStr,tempList);
//                    }
//                    billMap.get(timeStr).add(billVo);
//                    return billMap;
//                })
//                .forEach(map-> System.out.println(map.toString()));

        for(BillVo billVo:vos){
            String timeStr = format.format(billVo.getRecordTime());
            if(CollUtil.isEmpty(billMap.get(timeStr))){
                List<BillVo> tempList = new ArrayList<>();
                tempList.add(billVo);
                billMap.put(timeStr,tempList);
            }else {
                billMap.get(timeStr).add(billVo);
            }

        }
        if (billMap.isEmpty()){
            return ResponseResult.okResult();
        }
        Map<String, List<BillVo>> sortMapByKey = sortMapByKey(billMap);
        ArrayList<RecordVo> recordVos = new ArrayList<>();
        for (Map.Entry<String,List<BillVo>> item : sortMapByKey.entrySet()){
            RecordVo recordVo = new RecordVo();
            recordVo.setTime(item.getKey());
            recordVo.setChildren(item.getValue());
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal outcome = BigDecimal.ZERO;
            for (BillVo bv:item.getValue()){
                if (bv.getCategoryId().equals(SystemConstants.INCOME)){
                    income = income.add(bv.getAmount());
                }
                else if (bv.getCategoryId().equals(SystemConstants.OUTCOME)){
                    outcome = outcome.add(bv.getAmount());
                }
            }
            recordVo.setIncome(income);
            recordVo.setOutcome(outcome);
            recordVos.add(recordVo);
        }

        return ResponseResult.okResult(recordVos);
    }

    @Override
    public ResponseResult getMonthRecord(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Bill> billLambdaQueryWrapper = new LambdaQueryWrapper<>();
        try {
            Date parseDate = format.parse(date);
            System.out.println(DateUtil.month(parseDate));
            System.out.println(DateUtil.year(parseDate));
            billLambdaQueryWrapper.eq(Bill::getUid,userId)
                    .eq(Bill::getDelFlag,SystemConstants.DEL_FLG_NORMAL)
                    .apply("MONTH(record_time) = {0}", DateUtil.month(parseDate)+1)
                    .apply("YEAR(record_time) = {0}",DateUtil.year(parseDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Bill> bills = list(billLambdaQueryWrapper);

        List<BillVo> billVos = BeanCopyUtils.copyBeanList(bills, BillVo.class);
        List<BillVo> vos = billVos.stream()
                //获取账户名
                .map(billVo -> billVo.setAccountName(accountDao.queryAccountByAid(billVo.getAccountId())))
                //获取操作名
                .map(billVo -> billVo.setCategoryName(categoryDao.queryCategoryById(billVo.getCategoryId())))
                //获取标签名
                .map(billVo -> billVo.setTagName(tagDao.queryTagById(billVo.getTagId())))
//                //获取对应余额
//                .map(billVo -> billVo.setCurBalance(accountDao.queryAmountByAid(billVo.getAid()).subtract(billVo.getAmount())))
                .collect(Collectors.toList());
        ListUtil.sortByProperty(vos,"recordTime");
        HashMap<String, List<BillVo>> billMap = new HashMap<>();
//        vos.stream()
//                .map(billVo -> {
//                    String timeStr = format.format(billVo.getRecordTime());
//                    if(CollUtil.isEmpty(billMap.get(timeStr))){
//                        List<BillVo> tempList = new ArrayList<>();
//                        tempList.add(billVo);
//                        billMap.put(timeStr,tempList);
//                    }
//                    billMap.get(timeStr).add(billVo);
//                    return billMap;
//                })
//                .forEach(map-> System.out.println(map.toString()));

        for(BillVo billVo:vos){
            String timeStr = format.format(billVo.getRecordTime());
            if(CollUtil.isEmpty(billMap.get(timeStr))){
                List<BillVo> tempList = new ArrayList<>();
                tempList.add(billVo);
                billMap.put(timeStr,tempList);
            }else {
                billMap.get(timeStr).add(billVo);
            }

        }
        if (billMap.isEmpty()){
            return ResponseResult.okResult();
        }
        Map<String, List<BillVo>> sortMapByKey = sortMapByKey(billMap);
        ArrayList<RecordVo> recordVos = new ArrayList<>();
        for (Map.Entry<String,List<BillVo>> item : sortMapByKey.entrySet()){
            RecordVo recordVo = new RecordVo();
            recordVo.setTime(item.getKey());
            recordVo.setChildren(item.getValue());
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal outcome = BigDecimal.ZERO;
            for (BillVo bv:item.getValue()){
                if (bv.getCategoryId().equals(SystemConstants.INCOME)){
                    income = income.add(bv.getAmount());
                }
                else if (bv.getCategoryId().equals(SystemConstants.OUTCOME)){
                    outcome = outcome.add(bv.getAmount());
                }
            }
            recordVo.setIncome(income);
            recordVo.setOutcome(outcome);
            recordVos.add(recordVo);
        }
        return ResponseResult.okResult(recordVos);
    }

    @Override
    public ResponseResult getRecordByAccount(Long aid) {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Bill::getDelFlag,SystemConstants.DEL_FLG_NORMAL)
                .eq(Bill::getAccountId,aid);
        List<Bill> list = list(queryWrapper);
        List<BillVo> billVos = BeanCopyUtils.copyBeanList(list, BillVo.class);
        List<BillVo> vos = billVos.stream()
                //获取账户名
                .map(billVo -> billVo.setAccountName(accountDao.queryAccountByAid(billVo.getAccountId())))
                //获取操作名
                .map(billVo -> billVo.setCategoryName(categoryDao.queryCategoryById(billVo.getCategoryId())))
                //获取标签名
                .map(billVo -> billVo.setTagName(tagDao.queryTagById(billVo.getTagId())))
//                //获取对应余额
//                .map(billVo -> billVo.setCurBalance(accountDao.queryAmountByAid(billVo.getAid()).subtract(billVo.getAmount())))
                .collect(Collectors.toList());
        return ResponseResult.okResult(vos);
    }

    public static <T> Map<String, T> sortMapByKey(Map<String, T> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, T> sortMap = new TreeMap<String, T>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

}

