package com.lzh.financial.code.domain.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * (User)表实体类
 *
 * @author makejava
 * @since 2022-10-04 20:25:39
 */
@Data
@SuppressWarnings("serial")
public class User extends Model<User> {
    //用户id
    private Long uid;
    //用户名
    private String username;
    //用户密码
    private String password;
    //用户状态 1正常 0禁用
    private Integer status;
    //头像
    private String avatar;
    //0女 1男
    private Integer sex;
    //邮箱
    private String email;
    //昵称
    private String nickName;
    //类型 1普通用户 0管理员
    private Integer type;
    //手机号
    private String phoneNumber;
    //
    private Date createTime;
    //
    private Date updateTime;
    //
    private String updateBy;




    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.uid;
    }
    }

