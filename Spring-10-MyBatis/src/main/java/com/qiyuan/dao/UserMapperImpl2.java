package com.qiyuan.dao;

import com.qiyuan.entity.User;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.List;

/**
 * @ClassName UserMapperImpl2
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/27 18:23
 * @Version 1.0
 **/
public class UserMapperImpl2 extends SqlSessionDaoSupport implements UserMapper{
    public List<User> getUserList() {
        SqlSession sqlSession = getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = mapper.getUserList();
        return userList;
    }
}
