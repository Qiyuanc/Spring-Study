package com.qiyuan.dao;

import com.qiyuan.entity.User;

import java.util.List;

public interface UserMapper {
    // 查询全部用户
    List<User> getUserList();
    // 新增用户
    int addUser(User user);
    // 删除用户
    int deleteUser(int id);
}
