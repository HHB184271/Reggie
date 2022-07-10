package com.example.reggie.common;

/**
 * @Author HHB
 * @Date: 2022/7/6 13:58
 * @Description: 基于TreadLocal的工具类用于保存当前用户的ID
 * @Version 1.0
 */
public class BaseContext {
    /***
     * ThreadLocal设置值以便整个线程都可以调用到局部变量，
     *          但是有内存泄漏的风险
     */
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
