package com.qiyuan.entity;

/**
 * @ClassName Hello
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/21 13:30
 * @Version 1.0
 **/
public class Hello {
    private String str;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "str='" + str + '\'' +
                '}';
    }
}
