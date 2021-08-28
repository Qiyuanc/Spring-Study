package com.qiyuan.dao;

/**
 * @ClassName UserDaoMysqlImpl
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/20 20:58
 * @Version 1.0
 **/
public class UserDaoMysqlImpl  implements UserDao{
    public int getUser() {
        System.out.println("Mysql查询用户信息");
        return 1;
    }
}
