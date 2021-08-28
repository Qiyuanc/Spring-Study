package com.qiyuan.rent;

/**
 * @ClassName com.qiyuan.rent.Client
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/25 14:56
 * @Version 1.0
 **/
// 我就是客户！
public class Client {
    public static void main(String[] args){
        // 普通代理要求不能 new 真实对象，所以这里不算
        Host host = new Host();
        // 房东把房子交给代理了
        RentProxy rentProxy = new RentProxy(host);
        // 我们找代理租房，简单的租房操作其实背后有一条龙服务！
        rentProxy.rent();
    }
}
