package com.lzh.financial.code.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.financial.code.constants.SystemConstants;
import com.lzh.financial.code.dao.*;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.dto.AccountDto;
import com.lzh.financial.code.domain.dto.MailProp;
import com.lzh.financial.code.domain.entity.Account;
import com.lzh.financial.code.domain.entity.Bill;
import com.lzh.financial.code.domain.entity.Notice;
import com.lzh.financial.code.domain.entity.User;
import com.lzh.financial.code.domain.vo.AccountVo;
import com.lzh.financial.code.domain.vo.BillVo;
import com.lzh.financial.code.domain.vo.PageVo;
import com.lzh.financial.code.enums.AppHttpCodeEnum;
import com.lzh.financial.code.service.AccountService;
import com.lzh.financial.code.service.BillService;
import com.lzh.financial.code.utils.BeanCopyUtils;
import com.lzh.financial.code.utils.IDMaker;
import com.lzh.financial.code.utils.SecurityUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
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

    @Autowired
    private BillService billService;


    @Autowired
    private UserDao userDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private AccountDao accountDao ;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private TagDao tagDao;


    @Override
    public ResponseResult addAccount(AccountDto accountDto) {
        //获取当前登录用户信息
        Long uid = SecurityUtils.getUserId();
        //转换对象类型
        Account account = BeanUtil.toBean(accountDto, Account.class);
        //生成账户id
        Long aid = IDMaker.generateId();
        if (accountDto.getTypeId()==SystemConstants.CREDIT_ACCOUNT){
            account.setLastAmount(accountDto.getAmount());
        }
        account.setUid(uid);
        account.setId(aid);
        save(account);
        //添加账户后，根据账户类型，对总资产数据进行更新
        if(account.getTypeId()==4){
            //债权账户
            Bill bill = new Bill();
            bill.setBid(IDMaker.generateId());
            bill.setUid(SecurityUtils.getUserId());
            bill.setAmount(account.getAmount());
            bill.setAccountId(account.getId());
            bill.setCategoryId(SystemConstants.OUTCOME);
            bill.setTagId(5);
            bill.setRecordTime(new DateTime());
            bill.setRemark("账户变更");
            billService.save(bill);
        }
        else{
            Bill bill = new Bill();
            bill.setBid(IDMaker.generateId());
            bill.setUid(SecurityUtils.getUserId());
            bill.setAmount(account.getAmount());
            bill.setAccountId(account.getId());
            bill.setCategoryId(SystemConstants.INCOME);
            bill.setTagId(5);
            bill.setRecordTime(new DateTime());
            bill.setRemark("账户变更");
            billService.save(bill);
        }
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
        LambdaQueryWrapper<Bill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Bill::getDelFlag,SystemConstants.DEL_FLG_NORMAL);
        List<Bill> list = billService.list(queryWrapper);
        List<Long> collect = list.stream()
                .map(bill -> bill.getAccountId())
                .collect(Collectors.toList());
        for(Long accountId : longs){
            //判断想要删除的账户中是否存在关联账单，如存在，要求清除相关账单后才可删除。
            if (collect.contains(accountId)){
                return ResponseResult.errorResult(AppHttpCodeEnum.DELETE_FAIL);
            }
        }
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
                .map(accountVo -> {
                    LambdaQueryWrapper<Bill> billLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    billLambdaQueryWrapper.eq(Bill::getAccountId, accountVo.getId())
                            .eq(Bill::getDelFlag,SystemConstants.DEL_FLG_NORMAL);
                    List<Bill> billList = billService.list(billLambdaQueryWrapper);
                    List<BillVo> billVos = BeanCopyUtils.copyBeanList(billList, BillVo.class);
                    List<BillVo> tempList = billVos.stream()
                            //获取账户名
                            .map(billVo -> billVo.setAccountName(accountDao.queryAccountByAid(billVo.getAccountId())))
                            //获取操作名
                            .map(billVo -> billVo.setCategoryName(categoryDao.queryCategoryById(billVo.getCategoryId())))
                            //获取标签名
                            .map(billVo -> billVo.setTagName(tagDao.queryTagById(billVo.getTagId())))
//                //获取对应余额
//                .map(billVo -> billVo.setCurBalance(accountDao.queryAmountByAid(billVo.getAid()).subtract(billVo.getAmount())))
                            .collect(Collectors.toList());
                    return accountVo.setChildren(tempList);
                })
                .collect(Collectors.toList());
        return ResponseResult.okResult(new PageVo(vos,accountPage.getTotal()));
    }

    @Override
    public ResponseResult queryById(Long aid) {
        if (aid==null && aid==0){
            return ResponseResult.errorResult(500,"系统错误");
        }
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getId,aid)
        .eq(Account::getDelFlag,SystemConstants.DEL_FLG_NORMAL);
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




    @Override
    public ResponseResult needNotified() {
        //根据传入用户，确定所要保存的通知以及通知对象

        Long userId = SecurityUtils.getUserId();
        String subject = "帐房先生·财务预警";
        //从数据库中读取所有信用账户的账单日/还款日信息
        LambdaQueryWrapper<Account> accountLambdaQueryWrapper = new LambdaQueryWrapper<>();
        accountLambdaQueryWrapper.eq(Account::getDelFlag,SystemConstants.DEL_FLG_NORMAL)
                .eq(Account::getTypeId,SystemConstants.CREDIT_ACCOUNT);
        List<Account> creditAccountList = baseMapper.selectList(accountLambdaQueryWrapper);
        //获取提前一天的日期
        int day = DateUtil.thisDayOfMonth()+1;
        //判断是否需要提醒，如果需要，则发送 邮箱/短信 通知
        //判断1：账单日前一天
        ArrayList<String> list = new ArrayList<>();
        ArrayList<User> userList = new ArrayList<>();
        HashMap<String, List<String>> billingMap = new HashMap<>();
        creditAccountList.stream()
                //获取满足通知条件的账户
                .filter(account -> account.getBillingDay().equals(day))
                .map(account -> {
                    //获取通知对象
                    User user = userDao.selectById(account.getUid());
                    //list保存所需要通知给用户的具体账户
                    list.add(account.getName());
                    //map保存用户和对应账户。
                    billingMap.put(user.getEmail(),list);
                    return null;
                })
                .forEach(item-> System.out.println());
        Long aLong1 = IDMaker.generateId();
        Long aLong2 = IDMaker.generateId();
        for (Map.Entry<String,List<String>> item:billingMap.entrySet()){
            String email = item.getKey();
            String text = "您的账户"+item.getValue().toString()+"的账单日即将到了，截止至"
                    + day +"日!";

            rabbitTemplate.convertAndSend("send.mail", new MailProp(email,subject,text));
            Notice notice = new Notice();
            notice.setId(aLong1);
            User user = userDao.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
            notice.setUid(user.getUid());
            notice.setEmail(email);
            notice.setContent(text);
            notice.setNoticeTime(new DateTime());
            notice.setIsNew(SystemConstants.IS_NEW);
            noticeDao.insert(notice);
//            mailSendUtil.sendTextMailMessage(email,subject,text);

        }
        //判断2：还款日前一天
        ArrayList<String> repaymentList = new ArrayList<>();
//        ArrayList<User> userList = new ArrayList<>();
        HashMap<String, List<String>> repaymentMap = new HashMap<>();
        creditAccountList.stream()
                //获取满足通知条件的账户
                .filter(account -> account.getRepaymentDay().equals(day))
                .map(account -> {
                    //获取通知对象
                    User user = userDao.selectById(account.getUid());
                    //list保存所需要通知给用户的具体账户
                    repaymentList.add(account.getName());
                    //map保存用户和对应账户。
                    repaymentMap.put(user.getEmail(),repaymentList);
                    return null;
                })
                .forEach(item-> System.out.println());
        for (Map.Entry<String,List<String>> item:repaymentMap.entrySet()) {

            String email = item.getKey();

            String text = "您的账户" + item.getValue().toString() + "的还款日即将到了，截止至"
                    + day + "日!";
            rabbitTemplate.convertAndSend("send.mail", new MailProp(email, subject, text));
//            mailSendUtil.sendTextMailMessage(email,subject,text);
            Notice notice = new Notice();
            notice.setId(aLong2);
            User user = userDao.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
            notice.setUid(user.getUid());
            notice.setEmail(email);
            notice.setContent(text);
            notice.setIsNew(SystemConstants.IS_NEW);
            notice.setNoticeTime(new DateTime());
            noticeDao.insert(notice);

        }
        return ResponseResult.okResult();
    }

    //计算账单
    private BigDecimal computedBilling(Account creditCord){
        //计算一个账单周期内的消费（最大还款额）：上个月的余额 - 本月的余额
        BigDecimal repaymentAmount = creditCord.getLastAmount().subtract(creditCord.getAmount());
        // 设置上月余额
        creditCord.setLastAmount(creditCord.getAmount());
        return repaymentAmount;
    }

    //阈值判断方法
    public Integer needWarn(Long accountId){
        //获取对应账户的预警值
        Account account = baseMapper.selectById(accountId);
        BigDecimal threshold = account.getThreshold();
        BigDecimal amount = account.getAmount();
        if(amount!=null&&threshold!=null){
            if (amount.compareTo(threshold)==-1){
                //低于预警值，发出警告
                return SystemConstants.NEED_WARN;
            }
        }

        return SystemConstants.NO_NEED_WARN;
    }
}

