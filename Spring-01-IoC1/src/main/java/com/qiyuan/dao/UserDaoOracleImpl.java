package com.qiyuan.dao;

/**
 * @ClassName UserDaoOracleImpl
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/20 21:03
 * @Version 1.0
 **/
public class UserDaoOracleImpl implements UserDao{
    public int getUser() {
        System.out.println("Oracle查询用户信息");
        return 2;
    }
}
