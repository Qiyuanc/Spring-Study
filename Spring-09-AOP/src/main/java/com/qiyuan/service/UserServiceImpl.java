package com.qiyuan.service;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/26 17:55
 * @Version 1.0
 **/
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

    public void select() {
        System.out.println("查询用户！");
    }
}
