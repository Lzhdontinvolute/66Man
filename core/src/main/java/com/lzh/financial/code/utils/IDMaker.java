package com.lzh.financial.code.utils;

import cn.hutool.core.convert.Convert;

public class IDMaker {
    public static Long generateId(){
        int hashCode = java.util.UUID.randomUUID().toString().hashCode();
        if (hashCode <0){
            hashCode=-hashCode;
        }
        // 0 代表前面补充0
        // 10 代表长度为10
        // d 代表参数为正数型
        String format = String.format("%010d", hashCode).substring(0,10);
        System.out.println(format);
        return Convert.toLong(format);
    }
}
