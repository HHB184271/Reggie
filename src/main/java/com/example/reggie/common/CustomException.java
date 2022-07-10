package com.example.reggie.common;

/**
 * @Author HHB
 * @Date: 2022/7/6 22:31
 * @Description: 自定义业务异常
 * @Version 1.0
 */
public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }
}
