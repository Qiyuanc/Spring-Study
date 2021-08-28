package com.qiyuan.user;

/**
 * @ClassName UserServiceImplProxy
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/25 17:03
 * @Version 1.0
 **/
// Proxy 角色
public class UserServiceProxy implements UserService{
    // 要代理的对象！
    private UserService userService;

    // 不要直接 new 对象！添加 set 方法让 Spring 注入！连起来了！
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void add() {
        // 就当是 before 吧！
        printLog("[Debug]:进行了Add操作");
        userService.add();
    }

    public void delete() {
        printLog("[Debug]:进行了Delete操作");
        userService.delete();
    }

    public void update() {
        printLog("[Debug]:进行了Update操作");
        userService.update();
    }

    public void query() {
        printLog("[Debug]:进行了Query操作");
        userService.query();
    }

    public void printLog(String msg){
        System.out.println("[Debug]:进行了"+msg+"操作");
    }
}
