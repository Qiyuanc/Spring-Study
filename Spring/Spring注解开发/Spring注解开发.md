## Spring注解开发

> 在配置 Spring 时，注释比XML好吗？
>
> 基于注释的配置的引入提出了这样一个问题：这种方法是否比 XML “更好”。**简短的回答是“视情况而定”。长的回答是每种方法都有其优缺点，通常由开发人员决定哪种策略更适合他们。**由于它们的定义方式，注释在其声明中提供了大量的上下文，从而使配置更短、更简洁。然而，XML 擅长在不接触源代码或重新编译的情况下连接组件。一些开发人员更喜欢让连接靠近源代码，而其他人则认为带注释的类不再是POJO，而且配置变得分散，更难控制。
>
> 无论选择哪种，Spring 都可以容纳这两种风格，甚至可以将它们混合在一起。值得指出的是，通过它的 JavaConfig 选项，Spring 允许以非侵入性的方式使用注释，而不涉及目标组件源代码，而且，在工具方面， Spring Tools for Eclipse 支持所有配置样式。

上一节初次了解了 Spring 的注解，和如何使用注解进行自动装配。注解的用处还有很多，这里新建一个 Spring-06-Annotation 项目继续学习注解的使用。

### 1. 配置注解环境

在 Spring4 之后，要使用注解必须导入 spring-aop 的包。不过之前一直使用的 spring-webmvc 依赖已经包含了 aop 的包，就非常方便了

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.9</version>
</dependency>
```

创建配置文件 applicationContext.xml，然后就是和上节一样的，在配置文件中添加注解的约束

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <!--开启注解支持-->
    <context:annotation-config/>

</beans>
```

同时还可以使用

```xml
	<!--指定扫描包下的注解，这个包下的注解就会生效-->
    <context:component-scan base-package="com.qiyuan.entity"/>
```

**注意**：如果配置了 < context : component-scan > 那么 < context : annotation-config /> 标签就可以不用再xml中配置了，因为前者包含了后者。不过为了看起来好看还是写着吧。

### 2. Bean注册@Component

使用 @Component 注解，可以将一个类交给 Spring 管理（注册为组件 Component ），即创建一个 bean 对象，默认的 id 就是类名（小写）

在 com.qiyuan.entity 包下新建实体类 User，同时使用 @Component 注解

```java
@Component
// 相当于 <bean id="user" class="com.qiyuan.entity.User"/>
public class User {
    public String name = "Qiyuanc";
}
```

还是按照之前的方式获取对象

```java
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        User user = context.getBean("user", User.class);
        System.out.println(user.name);
    }
}
// 执行结果
// Qiyuanc
```

也可以给 @Component 设置 value，也就是 bean 的 id，如

```java
@Component(value = "user1")
// 相当于 <bean id="user" class="com.qiyuan.entity.User"/>
public class User {
    public String name = "Qiyuanc";
}
```

这样获取对象的时候就可以通过 user1 来获取了，user 就不存在了。

### 3. 属性注入@Value

上面的 name 是预先设置好的值，那想要用注解进行属性注入该怎么办呢？

那就要使用 @Value 注解

```java
@Component
// 相当于 <bean id="user" class="com.qiyuan.entity.User"/>
public class User {
    @Value("QiyuancByValue")
    // 相当于 <property name="name" value="QiyuancByValue"/>
    public String name;
}
```

**注意**：都给我整晕了，@Value 是用于基本类型的属性注入；如果要注入依赖（对象），用的是自动装配，如 @Resource！

### 4. @Component衍生注解

这里为了让其他包中的注解生效，还要在 applicationContext.xml 中添加

```xml
	<context:component-scan base-package="com.qiyuan"/>
```

加了之后在下面添加完注解就能看到代表被 Spring 管理的小叶子了。

@Component 有几个衍生的注解，名字不同但功能相同，用在 MVC 架构的不同层。

- 在 Dao 层，使用 @Repository

  ```java
  @Repository
  public class UserDao {
  }
  ```

- 在 Service 层，使用 @Service

  ```java
  @Service
  public class UserService {
  }
  ```

- 在 Controller 层，使用 @Controller

  ```java
  @Controller
  public class UserController {
  }
  ```

**这四个注解的功能都是一样的，都是代表将某个类注册到 Spring 中并装配！**

### 5. 自动装配注解

就是上节的内容啦，这里再复习一下。

自动装配的注解有三个

```java
// 自动寻找并装配 先 byType 后 byName
@Autowired 
// 配合 @Autowired 使用，指定对象的 id 去装配
@Qualifier
// 集合体 先按两种方式找，找不到还可以设置 name
@Resource
```

### 6. 作用域注解@Scope

使用 @Scope 注解设置作用域，就和 bean 标签中的 scope 属性一样

```java
@Component
//@Scope("singleton")
@Scope("prototype")
public class User {
    @Value("QiyuancByValue")
    public String name;
}
```

也是之前说的 singleton 单例和 prototype 原型，两种。不显式设置当然还是单例的啦。

### 7. 总结

XML 与注解两种配置方式对比

- XML：更加万能，适用于任何场合，且维护更方便！
- 注解：只能在对应的类上使用，维护也比较复杂！

XML 与注解的最佳实践

- 用 XML 来管理 bean
- 注解只负责属性的注入

这样一来可以在 XML 中看到有哪些 bean 是给 Spring 管理了，也不用去注意其中的属性注入，是一种比较好的方式🤗。
