package com.qiyuan.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName AnnotationAspect
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/26 23:10
 * @Version 1.0
 **/
// 声明这个类是一个切面！
@Aspect
@Component
public class AnnotationAspect {

    // 和 XML 中一样，通过 execution 表达式设置切入点
    @Before("execution(* com.qiyuan.service.UserServiceImpl.*(..))")
    @Bean
    public void Before(){
        System.out.println("=====方法执行前=====");
    }
    @After("execution(* com.qiyuan.service.UserServiceImpl.*(..))")
    @Bean
    public void After(){
        System.out.println("=====方法执行后=====");
    }

}
