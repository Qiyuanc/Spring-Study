package com.qiyuan.log;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @ClassName Log
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/26 17:57
 * @Version 1.0
 **/
public class BeforeLog implements MethodBeforeAdvice {
    // method: 要执行的目标对象的方法 | the method being invoked
    // args: 方法的参数 | the arguments to the method
    // target: 目标对象 | the target of the method invocation
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(target.getClass().getName()+" 的 "+method.getName()+" 方法被执行了！");
    }
}
