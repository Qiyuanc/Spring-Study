package com.qiyuan.entity;

/**
 * @ClassName User
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/21 15:31
 * @Version 1.0
 **/
public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
