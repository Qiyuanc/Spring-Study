package com.qiyuan.user;

// Subject 抽象角色
public interface UserService {
    // 对应 Dao 层的增删改查！
    public void add();
    public void delete();
    public void update();
    public void query();
}
