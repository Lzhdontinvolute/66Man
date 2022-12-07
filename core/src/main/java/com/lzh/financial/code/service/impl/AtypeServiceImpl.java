package com.lzh.financial.code.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.financial.code.constants.SystemConstants;
import com.lzh.financial.code.dao.AccountDao;
import com.lzh.financial.code.dao.AtypeDao;
import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.domain.entity.Account;
import com.lzh.financial.code.domain.entity.Atype;
import com.lzh.financial.code.domain.vo.AtypeVo;
import com.lzh.financial.code.service.AtypeService;
import com.lzh.financial.code.utils.RedisCache;
import com.lzh.financial.code.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * (Atype)表服务实现类
 *
 * @author makejava
 * @since 2022-10-20 19:48:38
 */
@Service("atypeService")
public class AtypeServiceImpl extends ServiceImpl<AtypeDao, Atype> implements AtypeService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AccountDao accountDao;

    @Override
    public ResponseResult getAllType() {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Atype> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Atype::getUid,userId,SystemConstants.SYSTEM_DEFAULT);
        queryWrapper.eq(Atype::getDelFlag, SystemConstants.DEL_FLG_NORMAL);
        List<Atype> atypes = baseMapper.selectList(queryWrapper);
        List<AtypeVo> atypeVos = BeanUtil.copyToList(atypes, AtypeVo.class);
        return ResponseResult.okResult(atypeVos);
    }

    @Override
    public ResponseResult listBaseAccountType() {
//        Long userId = SecurityUtils.getUserId();
//        LambdaQueryWrapper<Atype> queryWrapper = new LambdaQueryWrapper<>();
////        queryWrapper.in(Atype::getUid,userId,SystemConstants.SYSTEM_DEFAULT);
//        queryWrapper.eq(Atype::getPid,SystemConstants.BASE_TYPE);
//        queryWrapper.eq(Atype::getDelFlag, SystemConstants.DEL_FLG_NORMAL);
//        List<Atype> atypes = baseMapper.selectList(queryWrapper);
        //从redis中获取基本类型
        List<Atype> baseAType =  redisCache.getCacheObject("type:account");
        List<AtypeVo> atypeVos = BeanUtil.copyToList(baseAType, AtypeVo.class);
        return ResponseResult.okResult(atypeVos);
    }

    public ResponseResult queryAccountTypeTree(){
        //获取用户ID，如果是管理员，那就显示所有分类
        Long userId = SecurityUtils.getUserId();

        LambdaQueryWrapper<Atype> wrapper = new LambdaQueryWrapper<>();
        List<Atype> list = baseMapper.selectList(wrapper);
        List<Atype> atypeTree = getAtypeTree(list,SystemConstants.ROOT_ID);
        return ResponseResult.okResult(atypeTree);

    }

    private List<Atype> getAtypeTree(List<Atype> list, long l) {
        List<Atype> collect = list.stream()
                .filter(atype -> atype.getPid().equals(l))
                .map(atype -> atype.setChildren(getChildren(atype.getId())))
                .collect(Collectors.toList());
        return collect;
    }

    private List<Account> getChildren(Integer tid) {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        //判断父子类型
        wrapper.eq(Account::getTypeId,tid);
        wrapper.eq(Account::getDelFlag,SystemConstants.DEL_FLG_NORMAL);
//        if (userId!=1L){
            //如果不是管理员，那么就只获取当前用户的账户类型
            wrapper.eq(Account::getUid,userId);
//        }
        List<Account> accountList = accountDao.selectList(wrapper);
        return accountList;
    }
}

