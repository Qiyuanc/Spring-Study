## Spring代理模式

之前提到，Spring 的两个关键点就是 IoC（控制反转） 和 AOP（面向切面编程），IoC 已经研究过了，接下里就到 AOP 了。不过在学习 Spring AOP 前，必须要了解一下代理模式，因为代理模式是 AOP 的核心。

代理模式可以分为静态代理和动态代理，新建 Spring-08-Proxy 项目研究一下（因为在学习 Spring 的过程中，就不额外开个分类了）。

### 1. 静态代理

#### 1.1 代理模式类图

> 代理模式（ Proxy Pattern ）是一个使用率非常高的模式，其定义如下：
>
> Provide a surrogate or placeholder for another object to control access to it.（为其他对象提供一种代理以控制对这个对象的访问。）

代理模式的通用类图为

<img src="F:\TyporaMD\Spring\Spring代理模式\image-20210825144110588.png" alt="image-20210825144110588" style="zoom:67%;" />

其中的三个角色为

- Subject 抽象角色：抽象主题类可以是抽象类也可以是接口，是一个最**普通的业务类型定义**；
- RealSubject 具体角色：也叫做被委托角色、被代理角色，是**业务逻辑的具体执行者**；
- Proxy 代理角色：也叫做委托类、代理类。它**负责对真实角色的应用**，把所有抽象主题类定义的方法限制 委托给真实主题角色实现，并且**在真实主题角色处理完毕前后做预处理和善后处理工作**。

#### 1.2 租房例子

这里用租房的例子说明一下：租房本来有房东和客户两个对象，但房东不想自己去出租房子（或客户找不到房东租房），就需要一个第三方房屋中介（出租代理）。

出租房子的类图为

<img src="F:\TyporaMD\Spring\Spring代理模式\image-20210825155541350.png" alt="image-20210825155541350" style="zoom:67%;" />

用代码实现一下，首先有一个出租接口，是具体的业务

```java
// 出租接口
// Subject：抽象角色，业务定义
public interface Rent {
    void rent();
}
```

要出租房子的房东就要实现这个接口

```java
// 房东角色，要出租房子
// RealSubject：业务的具体执行者
public class Host implements Rent{
    public void rent() {
        System.out.println("房东的房子租出去了！");
    }
}
```

这时候客户已经可以找房东租房了

```java
// 我就是客户！
public class Client {
    public static void main(String[] args){
        Host host = new Host();
        host.rent();
    }
}
// 执行结果
// 房东的房子租出去了！
```

不过现在房东不想自己去出租房子（或客户找不到房东租房），就需要一个引入出租代理

```java
// 出租代理，租房找他
// Proxy：代理角色，负责对真实角色的应用
public class RentProxy implements Rent{
    // 要代理的对象
    private Host host;

    public RentProxy(Host host) {
        this.host = host;
    }

    public void rent() {
        // 帮房东出租房子
        host.rent();
    }
}
```

这时候客户要想租房，就可以去找代理了

```java
// 我就是客户！
public class Client {
    public static void main(String[] args){
        // 普通代理要求不能 new 真实对象，所以这里不算
        Host host = new Host();
        // 房东把房子交给代理了
        RentProxy rentProxy = new RentProxy(host);
        // 我们找代理租房就好了
        rentProxy.rent();
    }
}
// 执行结果
// 房东的房子租出去了！
```

在这里代理的作用就是，房东把自己的房子交给代理（ +Host ），客户租房直接去找代理就行了。

这样看来代理的作用好像不大，不过上面提到

> Proxy 代理角色：也叫做委托类、代理类。它**负责对真实角色的应用**，把所有抽象主题类定义的方法限制 委托给真实主题角色实现，并且**在真实主题角色处理完毕前后做预处理和善后处理工作**。

没错，**代理可以在业务执行前进行预处理，或者业务完成后进行善后。**

让代理来点作用，出租前先带客户看看房，客户如果要租房就签合同，代理也要收钱

```java
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
```

这里想到了一个问题：客户找代理租房，是客户跟代理说 “你带我去看房”，“你跟我签合同”，“你收我钱”；还是代理跟客户说 “要租房？先去看房”，“可以就签合同吧”，“把钱付一下” 呢？显然应该是后者。

这里的区别就体现在，Client 中要主动调用 seeHouse、getContract、getCost 这种预处理和善后工作吗？显然不应该。这种工作应该让代理去负责，客户找到代理租房，代理直接一条龙服务！对应的就是在代理的 rent 方法中进行预处理和善后。

这时客户去找代理租房

```java
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
// 执行结果
/*
    中介带客户看房！
    确认要租，签合同了！
    房东的房子租出去了！
    出租完成，收米！
*/
```

房东只管出租他的房子，客户只需要找代理租房，烦人的事情都让代理干完了，减轻了客户和房东的负担，这就是代理模式。

#### 1.3 用户业务例子

这里再用一个之前项目中的 UserService 例子加深对代理模式的理解。

首先有一个 UserService 接口，它是业务层的接口，对应 Dao 层的对数据库的操作

```java
// Subject 抽象角色
public interface UserService {
    // 对应 Dao 层的增删改查！
    public void add();
    public void delete();
    public void update();
    public void query();
}
```

然后是接口的具体实现类，之前在实现类中进行了对 Dao 层的调用，这里简化一下（亿下）

```java
// RealSubject 真实对象
public class UserServiceImpl implements UserService{
    public void add() {
        System.out.println("增加用户！");
    }

    public void delete() {
        System.out.println("删除用户！");
    }

    public void update() {
        System.out.println("修改用户！");
    }

    public void query() {
        System.out.println("查询用户！");
    }
}
```

好了，现在的要求是，要在执行某个操作时输出日志信息，那要怎么办呢？

如果在 UserServiceImpl 类上修改，就是

```java
// RealSubject 真实对象
public class UserServiceImpl implements UserService{
    public void add() {
        System.out.println("[Debug]:进行了Add操作");
        System.out.println("增加用户！");
    }
    // 同上
    ...
}
```

这样好像完成了要求。但此时的 UserServiceImpl 类承担了不应该由它来做的事情，一个业务实现类，为什么要输出日志啊？这就增加了代码的耦合性。如果要添加的奇奇怪怪的功能更多，这个业务实现类中关键的业务实现部分就更加不起眼了。

所以这时候就需要代理了！增加 UserServiceProxy 类，输出日志这种事情，属于代理的预处理或善后工作

```java
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
        System.out.println(msg);
    }
}
```

**让代理来输出日志，保证了 UserServiceImpl 类的职责清晰！**假设用到了增加用户的方法

```java
// 客户端！
public class Client {
    public static void main(String[] args){
        // 这三步应该让 Spring 来干！不过不麻烦了
        UserService userService = new UserServiceImpl();
        UserServiceProxy proxy = new UserServiceProxy();
        proxy.setUserService(userService);
        // 增加用户
        proxy.add();
    }
}
// 执行结果
// [Debug]:进行了Add操作
// 增加用户！
```

可以看到通过调用代理来执行业务，成功输出了日志。而如果有其他的要求，也可以通过扩展代理类去实现！

这大概就是面向切面编程（ AOP ）的思想吧！

<img src="F:\TyporaMD\Spring\Spring代理模式\image-20210825175614248.png" alt="image-20210825175614248" style="zoom:67%;" />

**扩展**：差点和装饰模式搞混了！在装饰模式中，装饰类的作用就是一个特殊的代理类。

#### 1.4 静态代理小结

**代理模式的优点**

- **职责清晰**：真实角色就是实现实际的业务逻辑，不用关心其他非本职责的事务，通过后期的代理完成一件事务，附带的结果就是编程简洁清晰。

  在租房例子中体现在房东只管如何出租他房子，不用管其他乱七八糟的事情！

- **高扩展性**：具体角色是随时都会发生变化的，只要它实现了接口，无论它如何变化，代理类完全就可以在不做任何修改的情况下使用。

  租房例子中的房东只要实现了出租房子接口，无论他想出租公寓还是别墅，代理都能帮他出租！

-  **智能化**：还没有体现出来，这就要看动态代理了。

**代理模式的缺点**

- 这种代理其实是静态代理，问题就是每有一个真实角色，就需要一个对应的代理。当真实角色很多的时候，代码量就非常庞大了。这个问题就需要动态代理解决。

代理模式又可以扩展为**普通代理**和**强制代理**，这里先不仔细研究了，到时候继续设计模式之禅。

### 2. 动态代理

> **什么是动态代理?**
>
> 动态代理就是，在程序运行期间创建目标对象的代理对象，并对目标对象中的方法进行功能性增强的一种技术。在生成代理对象的过程中，目标对象不变，代理对象中的方法是目标对象方法的增强方法。可以理解为运行期间，对象中方法的动态拦截，在拦截方法的前后执行功能操作。

动态代理的原理比较复杂，涉及到反射和 JVM 的知识（我都不会！），所以只能先照葫芦画瓢先用起来。

动态代理实现的方式有两种：JDK 和 CGLib，这里使用 JDK 实现。

#### 2.1 代理类模板

要使用动态代理，就要用到 JDK 提供的 Proxy 类和 InvocationHandler 接口

- Proxy 类：用于生成被代理接口的代理类对象！
- InvocationHandler 接口：只有一个方法 invoke 要实现，就是这个方法去调用被代理接口的方法！

首先创建 ProxyHandler 类，这个类就是动态代理类，**用它来动态地创建某个接口（实现该接口的对象）的代理类**，所以要有一个 target 属性接收要被代理的接口

```java
// 用它来动态生成代理类！
public class ProxyHandler {
    // 被代理的接口
    private Object target;

	// 用注入的方式！
    public void setTarget(Object target) {
        this.target = target;
    }
}
```

然后要实现调用处理器 InvocationHandler 接口，让这个类具有调用被代理接口的方法的能力（通过反射）

```java
// 用它来动态生成代理类！
public class ProxyHandler implements InvocationHandler {
    // 被代理的接口
    private Object target;

    // 用注入的方式！
    public void setTarget(Object target) {
        this.target = target;
    }

    // 处理代理实例，调用被代理对象的方法！
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在这里调用了被代理对象的方法！
        // 就是这里进行 面向切面编程！
        Object result = method.invoke(target, args);
        return result;
    }
}
```

最后要通过 Proxy 类，获取被代理接口的代理类对象！

```java
// 用它来动态生成代理类！
public class ProxyHandler implements InvocationHandler {
    // 被代理的接口
    private Object target;

    // 用注入的方式！
    public void setTarget(Object target) {
        this.target = target;
    }

    // 生成代理类
    public Object getProxy(){
        // 用代理类，创建代理实例
        // 参数为 类加载器ClassLoader loader
        //      被代理的接口 Class<?>[] interfaces
        //      调用处理器对象 InvocationHandler h
        // 这个类就实现了调用处理器接口，所以传自己！
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),
                target.getClass().getInterfaces(),this);
    }

    // 处理代理实例，调用被代理对象的方法！
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在这里调用了被代理对象的方法！
        // 就是这里进行 面向切面编程！
        Object result = method.invoke(target, args);
        return result;
    }
}
```

这就是一个动态代理类的模板了，下面来使用一下。

#### 2.2 租房动态代理

把租房例子用动态代理改写一下！

首先，租房接口没有变，房东也还是那个房东

```java
public interface Rent {
    void rent();
}
```

```java
public class Host implements Rent{
    public void rent() {
        System.out.println("房东的房子租出去了！");
    }
}
```

现在，没有手动写的代理类 RentProxy 了，要通过动态代理去获取

```java
public class Client {
    @Test
    public void rentTest(){
        // 真实角色
        Rent host = new Host();
        // 代理角色，不存在！怎么办？找动态代理类要！
        ProxyHandler ph = new ProxyHandler();
        // 传入要被代理的对象
        ph.setTarget(host);
        // 动态生成对应的代理类！
        Rent proxy = (Rent) ph.getProxy();
        // 使用被代理对象的方法
        proxy.rent();
    }
}
// 执行结果
// 房东的房子租出去了！
```

我们并没有去写租房的代理类 RentProxy，也成功实现了代理！

不过这里没有扩展的功能，要想进行预处理和善后工作，就要找到调用了被代理对象方法的方法——invoke方法，在调用前后增加新操作

```java
// 用它来动态生成代理类！
public class ProxyHandler implements InvocationHandler {
	...

    // 处理代理实例，调用被代理对象的方法！
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在这里调用了被代理对象的方法！
        seeHouse();
        getContract();
        Object result = method.invoke(target, args);
        getCost();
        return result;
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
```

这时候客户再去找代理租房，结果就和之前一样了

```java
// Client 同上
// 执行结果
/*
    中介带客户看房！
    确认要租，签合同了！
    房东的房子租出去了！
    出租完成，收米！
*/
```

#### 2.3 用户业务动态代理

再把用户业务的例子用动态代理改写一下，要求还是加个日志！

UserService 接口和 UserServiceImpl 实现类不变，这里省略。

在动态代理类中增加扩展的日志方法，并且在 invoke 方法中使用

```java
// 用它来动态生成代理类！
public class ProxyHandler implements InvocationHandler {
	...
        
    // 处理代理实例，调用被代理对象的方法！
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在这里调用了被代理对象的方法！
        // 通过反射获取调用的方法的名字！
        printLog(method.getName());
        Object result = method.invoke(target, args);
        return result;
    }

    // 扩展功能，输出日志
    public void printLog(String msg){
        System.out.println("[Debug]:进行了"+msg+"操作");
    }
}
```

在客户端中通过动态代理调用 add 方法

```java
public class Client {
    @Test
    public void userServiceTest(){
        // 真实角色
        UserService userService = new UserServiceImpl();
        // 找动态代理类
        ProxyHandler ph = new ProxyHandler();
        // 传入要被代理的对象
        ph.setTarget(userService);
        // 动态生成对应的代理类！
        UserService proxy = (UserService) ph.getProxy();
        // 使用被代理对象的方法
        proxy.add();
        proxy.delete();
    }
}
// 执行结果
/*
    [Debug]:进行了add操作
    增加用户！
    [Debug]:进行了delete操作
    删除用户！
*/
```

没有写 UserServiceProxy 类，也进行了代理操作！在 invoke 方法中，还通过 method 的 getName 方法获取了外部调用方法的名字（ 原理还是反射😥），更加简洁了！

#### 2.4 动态代理小结

**动态代理的入口是 Proxy 类**，通过其中的 newProxyInstance 方法获取被代理接口的代理类，参数为

- `ClassLoader loader`：用哪个类加载器去加载代理对象（**？**），通过 Class 对象中的 getClassLoader 方法获取
- `Class<?>[] interfaces`：被代理的接口，通过 Class 对象的 getInterfaces 方法获取，可以获取到 Class 对象实现的所有接口
- `InvocationHandler h`：调用处理器对象，调用被代理接口中的方法时，会通过其中的 invoke 方法去执行；修改 invoke 方法就相当于修改了代理类

**动态代理的桥梁是 InvocationHandler 接口**，通过其中的 invoke 方法调用被代理接口中的方法，参数为

- `Object proxy`：具体的代理类对象，其中有被代理接口的方法！
- `Method method`：被调用的方法
- `Object[] args`：被调用方法的参数

**动态代理通过 Proxy 类创建具体的代理类，代理类又通过 InvocationHandler 接口中的 invoke 方法完成对被代理接口中的方法的调用。**

### 3. 总结

**代理模式**：由最简单的静态代理，可以扩展为普通代理和强制代理（这里没深入），然后是最强大的动态代理！

静态代理好是好，但要写代理类太麻烦！所以出现了动态代理，动态代理的原理有点深奥，类加载器 Java 虚拟机啥啥的，后面再研究吧！

不过现在还不知道动态代理除了添加日志外还有什么应用场景···就像上面的租房和用户业务，不还得对应不同的动态代理类的操作嘛？

麻了，后面再说吧😥！
