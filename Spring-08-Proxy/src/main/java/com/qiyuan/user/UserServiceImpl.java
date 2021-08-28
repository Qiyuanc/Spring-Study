package com.qiyuan.user;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/25 16:56
 * @Version 1.0
 **/
// RealSubject 真实对象
public class UserServiceImpl implements UserService{
    public void add() {
        System.out.println("增加用户！");
    }

    public void delete() {
        System.out.println("删除用户！");
    }

    public void update() {
        System.out.println("修改用户！");
    }

    public void query() {
        System.out.println("查询用户！");
    }
}
