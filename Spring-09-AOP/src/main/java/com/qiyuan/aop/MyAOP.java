package com.qiyuan.aop;

/**
 * @ClassName MyAOP
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/26 22:43
 * @Version 1.0
 **/
// 这就是一个切面！
public class MyAOP {

    public void Before(){
        System.out.println("=====方法执行前=====");
    }

    public void After(){
        System.out.println("=====方法执行后=====");
    }
}
