package com.qiyuan.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName ProxyHandler
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/25 23:08
 * @Version 1.0
 **/
// 用它来动态生成代理类！
public class ProxyHandler implements InvocationHandler {
    // 被代理的对象
    private Object target;

    // 用注入的方式！
    public void setTarget(Object target) {
        this.target = target;
    }

    // 生成代理类
    public Object getProxy(){
        // 用代理类，创建代理实例
        // 参数为 类加载器ClassLoader loader
        //      被代理的接口 Class<?>[] interfaces
        //      调用处理器对象 InvocationHandler h
        // 这个类就实现了调用处理器接口，所以传自己！
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                target.getClass().getInterfaces(),this);
    }

    // 处理代理实例，调用被代理对象的方法！
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在这里调用了被代理对象的方法！
        // 通过反射获取调用的方法的名字！
        printLog(method.getName());
        Object result = method.invoke(target, args);
        return result;
    }

    // 扩展功能，输出日志
    public void printLog(String msg){
        System.out.println("[Debug]:进行了"+msg+"操作");
    }

    // 预处理 before
    public void seeHouse(){
        System.out.println("中介带客户看房！");
    }

    // 应该算预处理
    public void getContract(){
        System.out.println("确认要租，签合同了！");
    }

    // 善后 after
    public void getCost(){
        System.out.println("出租完成，收米！");
    }
}
