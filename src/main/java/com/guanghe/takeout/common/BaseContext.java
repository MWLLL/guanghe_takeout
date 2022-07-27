package com.guanghe.takeout.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前登录用户的id
 * 以线程作为作用域，每个线程单独保存副本
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();


    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
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
