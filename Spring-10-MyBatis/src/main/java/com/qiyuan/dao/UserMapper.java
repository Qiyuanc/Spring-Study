package com.qiyuan.dao;

import com.qiyuan.entity.User;

import java.util.List;

public interface UserMapper {
    // 查询全部用户
    List<User> getUserList();
}
