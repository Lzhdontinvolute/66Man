package com.lzh.financial.code.service.impl;

import ch.qos.logback.core.pattern.ConverterUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.financial.code.constants.SystemConstants;
import com.lzh.financial.code.dao.AccountDao;
import com.lzh.financial.code.dao.AtypeDao;
import com.lzh.financial.code.domain.LoginUser;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.AccountDto;
import com.lzh.financial.code.domain.entity.Account;
import com.lzh.financial.code.domain.vo.AccountVo;
import com.lzh.financial.code.domain.vo.PageVo;
import com.lzh.financial.code.service.AccountService;
import com.lzh.financial.code.utils.BeanCopyUtils;
import com.lzh.financial.code.utils.IDMaker;
import com.lzh.financial.code.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.lzh.financial.code.enums.AppHttpCodeEnum.DELETE_FAIL;

/**
 * (Account)表服务实现类
 *
 * @author makejava
 * @since 2022-10-09 16:22:10
 */
@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements AccountService {

    @Autowired
    private AtypeDao atypeDao;

    @Override
    public ResponseResult addAccount(AccountDto accountDto) {
        //获取当前登录用户信息
        Long uid = SecurityUtils.getUserId();
        //转换对象类型
        Account account = BeanUtil.toBean(accountDto, Account.class);
        //生成账户id
        Long aid = IDMaker.generateId();
        account.setTypeId(accountDto.getTypeId());
        account.setUid(uid);
        account.setId(aid);
        save(account);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateAccount(Account account) {
        account.setUpdateTime(new DateTime());
//        account.setWarn(needWarn(account.getId()));
        updateById(account);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteAccount(Object aid) {
//        removeById(aid);
        Long[] longs = Convert.toLongArray(aid);
        //逻辑删除
        Account account = new Account();
        account.setDelFlag(SystemConstants.DEL_FLG_BANNED);
        UpdateWrapper<Account> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("del_flag",SystemConstants.DEL_FLG_NORMAL)
                .in("id",longs);
        boolean update = update(account, updateWrapper);
        if (update){
            return ResponseResult.okResult();
        }
        return ResponseResult.errorResult(DELETE_FAIL,DELETE_FAIL.getMsg());
    }

    @Override
    public ResponseResult listAllAccount(Integer pageNum, Integer pageSize, String accountName) {
        //获取登陆用户信息
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Account> accountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据账户名查询
        accountLambdaQueryWrapper.like(StringUtils.hasText(accountName),Account::getName,accountName);
        accountLambdaQueryWrapper.eq(Account::getUid,userId);
        accountLambdaQueryWrapper.eq(Account::getDelFlag,SystemConstants.DEL_FLG_NORMAL);
        Page<Account> accountPage = new Page<>(pageNum,pageSize);
        page(accountPage, accountLambdaQueryWrapper);
        List<Account> records = accountPage.getRecords();
        //根据tid获取账户类型名
//        String typeName = atypeDao.queryTypeNameByTid();
        List<AccountVo> accountVos = BeanCopyUtils.copyBeanList(records, AccountVo.class);
        List<AccountVo> vos = accountVos.stream()
                .map(accountVo ->
                        accountVo.setTypeName(atypeDao.queryTypeNameByTid(accountVo.getTypeId())))
                .map(accountVo -> accountVo.setWarn(needWarn(accountVo.getId())))
                .collect(Collectors.toList());
        return ResponseResult.okResult(new PageVo(vos,accountPage.getTotal()));
    }

    @Override
    public ResponseResult queryById(Long aid) {
        if (aid==null && aid==0){
            return ResponseResult.errorResult(500,"系统错误");
        }
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getId,aid);
        Account account = baseMapper.selectOne(wrapper);
        AccountVo accountVo = BeanCopyUtils.copyBean(account, AccountVo.class);
        return ResponseResult.okResult(accountVo);
    }

    @Override
    public Account getDefaultAccount() {
//        Long userId = SecurityUtils.getUserId();
        Account account = new Account();
        Long aid = IDMaker.generateId();
        account.setId(aid);
//        account.setUid(userId);
        account.setName("默认账户");
        account.setAmount(BigDecimal.valueOf(0));
        account.setThreshold(BigDecimal.valueOf(0));
        //现金账户
        account.setTypeId(1);
        return account;
    }

    //阈值判断方法
    public Integer needWarn(Long accountId){
        //获取对应账户的预警值
        Account account = baseMapper.selectById(accountId);
        BigDecimal threshold = account.getThreshold();
        BigDecimal amount = account.getAmount();
        if (amount.compareTo(threshold)==-1){
            //低于预警值，发出警告
            return SystemConstants.NEED_WARN;
        }
        return SystemConstants.NO_NEED_WARN;
    }
}

