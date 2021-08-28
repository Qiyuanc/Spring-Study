package com.qiyuan.rent;

/**
 * @ClassName Proxy
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/25 14:59
 * @Version 1.0
 **/
// 出租代理，租房找他
// Proxy：代理角色，负责对真实角色的应用
public class RentProxy implements Rent{
    // 要代理的对象
    private Host host;

    public RentProxy(Host host) {
        this.host = host;
    }

    public void rent() {
        // 客户找代理租房，先带去看房
        seeHouse();
        // 客户觉得可以，签合同
        getContract();
        // 帮房东出租房子
        host.rent();
        // 租完房子要收钱的
        getCost();
    }

    // 预处理 before
    public void seeHouse(){
        System.out.println("中介带客户看房！");
    }

    // 应该算预处理
    public void getContract(){
        System.out.println("确认要租，签合同了！");
    }

    // 善后 after
    public void getCost(){
        System.out.println("出租完成，收米！");
    }
}
