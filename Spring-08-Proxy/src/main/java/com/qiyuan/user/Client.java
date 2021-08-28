package com.qiyuan.user;

/**
 * @ClassName Client
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/25 17:07
 * @Version 1.0
 **/
// 客户端！
public class Client {
    public static void main(String[] args){
        // 这三步应该让 Spring 来干！不过不麻烦了
        UserService userService = new UserServiceImpl();
        UserServiceProxy proxy = new UserServiceProxy();
        proxy.setUserService(userService);
        // 增加用户
        proxy.add();
    }
}
