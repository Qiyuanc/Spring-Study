package com.qiyuan.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @ClassName User
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/24 13:03
 * @Version 1.0
 **/

@Component(value = "user1")
//@Scope("singleton")
@Scope("prototype")
public class User {
    @Value("QiyuancByValue")
    public String name;
}
