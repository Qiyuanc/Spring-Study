package com.qiyuan.rent;

/**
 * @ClassName com.qiyuan.rent.Host
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/25 14:55
 * @Version 1.0
 **/
// 房东角色，要出租房子
// RealSubject：业务的具体执行者
public class Host implements Rent{
    public void rent() {
        System.out.println("房东的房子租出去了！");
    }
}
