package com.lzh.financial.code.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    USERNAME_EXIST(501,"用户名已存在"),
    NICKNAME_EXIST(512,"用户名已存在"),
    PHONE_NUMBER_EXIST(502,"手机号已存在"),
    PHONE_NUMBER_NOT_NULL(502,"手机号不能为空"),
    PHONE_NUMBER_INVALID(502,"手机号不可用"),
    EMAIL_EXIST(503, "邮箱已存在"),
    EMAIL_INVALID(503, "邮箱不可用"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    CONTENT_NOT_NULL(506, "评论内容不能为空"),
    FILE_TYPE_ERROR(507, "文件类型错误"),
    USERNAME_NOT_NULL(508, "用户名不能为空"),
    PASSWORD_NOT_NULL(509, "密码不能为空"),
    EMAIL_NOT_NULL(510, "邮箱不能为空"),
    NICKNAME_NOT_NULL(511, "昵称不能为空"),
    LOGIN_ERROR(505,"用户名或密码错误"),
    TAG_NAME_NULL(506,"标签名不能为空"),
    DELETE_FAIL(506,"删除失败"),
    UPDATE_FAIL(506,"更新失败");
    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}