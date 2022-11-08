package com.lzh.financial.code.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeanCopyUtils {
    private BeanCopyUtils(){

    }

    public static <V> V copyBean(Object source,Class<V> clazz){
        //创建目标（VO）对象
        V result = null;
        try {

            result = clazz.newInstance();
            //进行拷贝
            BeanUtils.copyProperties(source,result);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <M,V> List<V> copyBeanList(List<M> list,Class<V> clazz){
        return  list.stream()
                .map(o->copyBean(o,clazz))
                .collect(Collectors.toList());

    }

}
