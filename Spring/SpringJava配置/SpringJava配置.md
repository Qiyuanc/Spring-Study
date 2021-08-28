## SpringJava配置

在 Spring 中，也可以不用 XML 文件，转而使用 Java 代码的方式配置。官方文档的例子

> ```java
> @Configuration
> public class AppConfig {
> 
>     @Bean
>     public MyService myService() {
>         return new MyServiceImpl();
>     }
> }
> ```
>
> The preceding `AppConfig` class is **equivalent** to the following Spring `<beans/>` XML:
>
> ```xml
> <beans>
>     <bean id="myService" class="com.acme.services.MyServiceImpl"/>
> </beans>
> ```

这两种方式效果是相同的。现在新建 Spring-07-JavaConfig 项目来试一试。

### 1. 测试环境搭建

在 com.qiyuan.entity 包下创建一个实体类 User，这里就不用 Lombok 了；同时给 name 属性注入值 Qiyuanc

```java
public class User {
    private String name;

    public String getName() {
        return name;
    }

    @Value("Qiyuanc")
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "User{" + "name='" + name + '\'' + '}';
    }
}
```

不使用 XML 文件，怎么才能把这个对象注册成一个 bean 呢（就算是使用注解，也需要在 XML 中开启注解和扫描包）？

### 2. 使用JavaConfig

#### 2.1 设置配置类

先在 com.qiyuan.config 包中创建一个 MyConfig 类，刚创建完这只是一个普通类，通过 @Configuration 注解把这个类设置为 Spring 的**配置类**，作用相当于 XML 文件！

```java
@Configuration
// 这个类虽然是配置类，但也会被 Spring 接管，因为 @Configuration 包含了 @Component
public class MyConfig {
}
```

设置完后就能看到这个类的旁边有小叶子了！

#### 2.2 注册Bean

然后在里面添加一个方法，直接返回一个 User 对象，并在方法上加上 @Bean 的注解；

```java
@Configuration
public class MyConfig {

    @Bean
    public User getUser(){
        return new User();
    }
}
```

这时这个方法旁边也出现了小叶子（点不进去不知道为什么），说明 bean 被 Spring 管理了！

> @Configuration 标注在类上，相当于把该类作为 XML 配置文件中的 <beans>
>
> @Bean 可理解为 XML 中的 <bean> 标签

#### 2.3 测试获取对象

这时候其实 bean 就已经配置好了，在测试方法中获取对象试一下。

不过获取的方式和 XML 不同，这里要先获取 AnnotationConfigApplicationContext 对象（注解配置应用容器），参数为配置类！

```java
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        User getUser = context.getBean("getUser", User.class);
        System.out.println(getUser);
    }
}
// 执行结果
// User{name='Qiyuanc'}
```

没有用 XML 配置也成功获取到对象了！

可以发现，在测试类中 getBean 的参数为 getUser，这就要说到加了 @Bean 注解的方法了

```java
    @Bean
    public User getUser(){
        // 返回值 = calss
        // 方法名 = id
        return new User();
    }
```

**加上 @Bean 注解后，这个方法就相当于一个 <bean> 标签！方法的返回值就是 bean 的 class，方法名就是 bean 的 id！**

#### 2.4 小结

**扩展**：进入 @Configuration 的源码查看，也能看到 @Component，说明**配置类也是一个组件，也会被 Spring 管理！**

```java
...
@Component
public @interface Configuration {
	...
}
```

**注意**：如果使用了 JavaConfig 的方式获取对象，则**对应的类上不需要添加 @Component 注解**，否则容器中会存在两个对象！

添加 @Component 把类设置为组件，上面的 MyConfig 类不变

```java
@Component
public class User {
    ...
}
```

用测试方法获取两个对象，比较它们是否相同

```java
public class MyTest {
    @Test
    public void Test2(){
        ApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        User user = context.getBean("user", User.class);
        User getUser = context.getBean("getUser", User.class);
        System.out.println(user);
        System.out.println(getUser);
        System.out.println(user==getUser);
    }
}
// 执行结果
// User{name='Qiyuanc'}
// User{name='Qiyuanc'}
// false
```

可以看到获取的其实是两个对象！name 相同是因为在 User 类中进行的属性注入。

**使用 JavaConfig 配置类时，IoC 容器并没有管理 User 类，管理的其实是 MyConfig 配置类，通过配置类获取到 getUser 对象！如果给 User 类加上 @Component 注解，则 IoC 容器也会管理它，默认产生的对象就是 user，所以会出现两个对象！**

### 3. 总结

使用 JavaConfig，Spring 就可以完全脱离 XML 文件配置（注解还是需要 XML 的），在 SpringBoot 中，这种配置方式用的将更多。

使用 JavaConfig 进行配置的三个步骤

1. 配置类 @Configuration
2. 需要管理的类
3. 添加对应类的 return 方法 @Bean，返回值是 class，方法名是 id

不过最后发现了一个很逆天的问题：**去掉 @Configuration 注解，一样能运行，但 @Bean 去掉就找不到对象了。**看来 @Bean 才是最关键的😵！

