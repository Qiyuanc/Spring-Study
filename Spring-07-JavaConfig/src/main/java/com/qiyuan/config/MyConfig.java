package com.qiyuan.config;

import com.qiyuan.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MyConfig
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/24 17:28
 * @Version 1.0
 **/
@Configuration
// 这个类虽然是配置类，但也会被 Spring 接管，因为 @Configuration 包含了 @Component
@ComponentScan(basePackages = "com.qiyuan.entity")
public class MyConfig {

    @Bean
    public User getUser(){
        // 返回值 = calss
        // 方法名 = id
        return new User();
    }
}
