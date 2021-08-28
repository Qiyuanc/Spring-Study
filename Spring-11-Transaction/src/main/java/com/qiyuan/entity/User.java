package com.qiyuan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @ClassName User
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/27 12:48
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private int id;
    private String name;
    private String pwd;
}
