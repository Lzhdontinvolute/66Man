package com.lzh.financial.code.handler.exception;



import com.lzh.financial.code.domain.ResponseResult;
import com.lzh.financial.code.enums.AppHttpCodeEnum;
import com.lzh.financial.code.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler  {

    @ExceptionHandler(SystemException.class)
    public ResponseResult systemException(SystemException e){
//        打印异常信息（@Slf4j）
        log.error("出现异常！",e);
        //封装异常信息返回到前段
        return ResponseResult.errorResult(e.getCode(),e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public ResponseResult exception(Exception e){
        //打印异常信息（@Slf4j）
        log.error("出现异常！ ",e);
        //封装异常信息返回到前段
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(),e.getMessage());
    }
}
