## SpringBean的作用域

和正常创建对象一样，在 Spring 中用 bean 配置的对象也有对应的作用域，可以在 bean 标签中通过 scope 属性进行设置

![image-20210823124951021](F:\TyporaMD\Spring\SpringBean的作用域\image-20210823124951021.png)

这里沿用上节的 Spring-04-DI 项目了解一下 bean 的作用域。

### 1. Singleton 单例

单例是默认的作用域，即如果不设置 scope 属性，则其默认为 singleton，显式设置为

```xml
<bean id="user" class="User" p:name="祈鸢" p:age="18" scope="singleton"/>
```

若一个 bean 的作用域设置为 singleton，则在程序运行过程中这个对象只有一个，任何对该对象的引用都是在引用同一个对象（**就是单例模式**）。

官方文档的图解

![image-20210823125448226](F:\TyporaMD\Spring\SpringBean的作用域\image-20210823125448226.png)

用测试方法测试一下，之前也做过这个测试，即从 bean 中获取一个对象两次，看看它们是否相同

```java
@Test
public void Test3(){
    ApplicationContext context = new ClassPathXmlApplicationContext("userbeans.xml");
    // 反射
    User qiyuan1 = context.getBean("user", User.class);
    User qiyuan2 = context.getBean("user", User.class);
    System.out.println(qiyuan1.hashCode());
    System.out.println(qiyuan2.hashCode());
    System.out.println(qiyuan1==qiyuan2);
}
// 执行结果
/*
    1007513
    1007513
    true
*/
```

可以看到 hashCode 相同，用 == 比较也是相同的。

### 2. Prototype 原型

通过将 scope 属性设置为 prototype 将某个对象的作用域设置为原型。

```xml
<bean id="user2" class="User" c:name="小祈" c:age="18" scope="prototype"/>
```

若一个 bean 被设置为了原型，则每个对该对象的引用都会创建一个新的该对象。**简单来说，就是这个 bean 是一个模板，用到的时候就按照模板创建一个出来。**

官方文档的图解

![image-20210823125948602](F:\TyporaMD\Spring\SpringBean的作用域\image-20210823125948602.png)

也写个测试方法测试一下，看看通过原型获取的对象是否相同

```java
@Test
public void Test4(){
    ApplicationContext context = new ClassPathXmlApplicationContext("userbeans.xml");
    // 反射
    User qiyuan1 = context.getBean("user2", User.class);
    User qiyuan2 = context.getBean("user2", User.class);
    System.out.println(qiyuan1.hashCode());
    System.out.println(qiyuan2.hashCode());
    System.out.println(qiyuan1==qiyuan2);
}
// 执行结果
/*
    766168
    766168
    false
*/
```

**注意**：一般来说，写实体类的时候只会添加构造器、get/set 方法、toString 方法，不会重写 equals 方法和 hashCode 方法。但我的实体类使用了 Lombok，直接 @Data 重写了这个两个方法，导致这里 hashCode 相同但使用 == 比较却为 false。

>  hashCode 是所有 java 对象的固有方法，如果不重载的话，返回的实际上是该对象在 jvm 的堆上的内存地址，而不同对象的内存地址肯定不同，所以这个 hashCode 也就肯定不同了。如果重载了的话，由于采用的算法的问题，有可能导致两个不同对象的 hashCode 相同。

### 3. 其他作用域

其他的作用域都是在 web 开发中使用的（ Only valid in the context of a web-aware Spring `ApplicationContext` ），与 web 中的几大作用域相同。

- request 与 web 中的 request 作用域相同，只在一次请求中有效（ single HTTP request ）

- session 与 web 中的 session 作用域相同，在会话建立后直到关闭前有效（ HTTP `Session` ）

- application 与 web 中的 ServletContext 作用域相同，在服务器开启后直到关闭前有效

websocket 还不知道是个什么东西，先不管了。

### 4. 总结

还有很多作用域要在 web 中使用，这里只能先了解一下单例 singleton 和原型 prototype 的作用域（还要特地写一篇😓），其他的后面用到再说。