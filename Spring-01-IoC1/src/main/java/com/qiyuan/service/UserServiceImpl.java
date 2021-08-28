package com.qiyuan.service;

import com.qiyuan.dao.UserDao;
import com.qiyuan.dao.UserDaoImpl;
import com.qiyuan.dao.UserDaoMysqlImpl;
import com.qiyuan.dao.UserDaoOracleImpl;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/20 19:11
 * @Version 1.0
 **/
public class UserServiceImpl implements UserService{

    // 更好的方式，要用那个实现就自己传进来
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public int getUser() {
        return userDao.getUser();
    }
}
