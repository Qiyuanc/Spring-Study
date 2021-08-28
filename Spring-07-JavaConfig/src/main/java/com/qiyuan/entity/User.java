package com.qiyuan.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @ClassName User
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/24 17:24
 * @Version 1.0
 **/
@Component
public class User {
    private String name;

    public String getName() {
        return name;
    }

    @Value("Qiyuanc")
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
