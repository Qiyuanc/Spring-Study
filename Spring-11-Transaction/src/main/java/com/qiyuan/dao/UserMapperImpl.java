package com.qiyuan.dao;

import com.qiyuan.entity.User;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

/**
 * @ClassName UserMapperImpl
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/27 16:26
 * @Version 1.0
 **/
public class UserMapperImpl implements UserMapper{

    private SqlSessionTemplate sqlSession;
    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 在这里进行封装！
    public List<User> getUserList() {
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
        User user = new User(6, "qiyuan666", "0723");
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        // 先新增，再删掉，所以查询的结果应该没有这个用户！
        mapper.addUser(user);
        mapper.deleteUser(6);
        List<User> userList = mapper.getUserList();
        return userList;
    }

    public int addUser(User user) {
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int row = mapper.addUser(user);
        return row;
    }

    public int deleteUser(int id) {
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int row = mapper.deleteUser(id);
        return row;
    }

}
