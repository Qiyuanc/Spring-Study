package com.qiyuan.dynamicProxy;

import com.qiyuan.rent.Host;
import com.qiyuan.rent.Rent;
import com.qiyuan.user.UserService;
import com.qiyuan.user.UserServiceImpl;
import org.junit.jupiter.api.Test;

/**
 * @ClassName Client
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/25 23:20
 * @Version 1.0
 **/
public class Client {
    @Test
    public void rentTest(){
        // 真实角色
        Rent host = new Host();
        // 代理角色，不存在！怎么办？找动态代理类要！
        ProxyHandler ph = new ProxyHandler();
        // 传入要被代理的对象
        ph.setTarget(host);
        // 动态生成对应的代理类！
        Rent proxy = (Rent) ph.getProxy();
        // 使用被代理对象的方法
        proxy.rent();
    }

    @Test
    public void userServiceTest(){
        // 真实角色
        UserService userService = new UserServiceImpl();
        // 找动态代理类
        ProxyHandler ph = new ProxyHandler();
        // 传入要被代理的对象
        ph.setTarget(userService);
        // 动态生成对应的代理类！
        UserService proxy = (UserService) ph.getProxy();
        // 使用被代理对象的方法
        proxy.add();
        proxy.delete();
    }
}
