package com.qiyuan.dao;

import com.qiyuan.entity.User;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

/**
 * @ClassName UserMapperImpl
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/27 16:26
 * @Version 1.0
 **/
public class UserMapperImpl implements UserMapper{
    // 原来的操作，使用 SqlSession 实现，现在使用 SqlSessionTemplate
    // 不过还是叫做 sqlSession 亲切！
    private SqlSessionTemplate sqlSession;

    // 添加 set 方法，以注入属性
    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 在这里进行封装！
    public List<User> getUserList() {
        // IoC 的思想！不用去 new 一个 sqlSession 了，注入后就能用！
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = mapper.getUserList();
        return userList;
    }
}
