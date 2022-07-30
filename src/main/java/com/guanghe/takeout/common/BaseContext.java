package com.guanghe.takeout.common;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前登录用户的id
 * 以线程作为作用域，每个线程单独保存副本
 */
@Slf4j
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();


    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        log.info("保存用户id：{}=====>线程号：{}",id,Thread.currentThread().getId());
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
