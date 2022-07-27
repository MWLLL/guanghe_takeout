package com.guanghe.takeout.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * 处理SQLIntegrityConstraintViolationException异常
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")){//判断是否包含Duplicate entry的sql异常
            String[] split = ex.getMessage().split(" ");//按空格分割'Duplicate entry 'xxx' for key 'employee.idx_username'异常信息
            String msg = split[2]+"已存在";//给前端传送已存在的账户：xxx已存在
            return R.error(msg);
        }

        return R.error("未知错误,请联系管理员");
    }

    /**
     * 异常处理方法
     * 处理CustomException自定义异常
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }
}
