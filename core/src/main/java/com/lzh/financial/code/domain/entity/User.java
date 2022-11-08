package com.lzh.financial.code.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (User)表实体类
 *
 * @author makejava
 * @since 2022-10-04 20:25:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
public class User extends Model<User> {
    //用户id
    @TableId
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

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(fill = FieldFill.INSERT)
    private String delFlag;

    @TableField(fill = FieldFill.INSERT_UPDATE)
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

