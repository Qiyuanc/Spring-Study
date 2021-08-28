package com.qiyuan.log;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @ClassName AfterLog
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/26 18:04
 * @Version 1.0
 **/
public class AfterLog implements AfterReturningAdvice {
    // 多了一个返回值的参数
    // returnValue: 方法调用的返回值 | the value returned by the method, if any
    // 其他参数都是一样的！
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("执行了 "+method.getName()+" 方法，返回值为 "+returnValue);
    }
}
