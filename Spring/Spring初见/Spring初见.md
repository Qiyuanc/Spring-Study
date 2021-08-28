## Spring初见

### 1. Spring简介

#### 1.1 Spring介绍

Spring 框架是由于软件开发的复杂性而创建的。Spring 使用的是基本的 JavaBean 来完成以前只可能由 EJB 完成的事情。然而，Spring 的用途不仅仅限于服务器端的开发。从简单性、可测试性和松耦合性角度而言，绝大部分 Java 应用都可以从 Spring 中受益。

**Spring的优点**

- Spring 是一个开源的免费的框架！
- Spring 是一个轻量级、非侵入式的框架！
- 核心思想：控制反转（ IoC ），面向切面编程（ AOP ）。
- 支持事务的处理和对框架的整合。

**总而言之：Spring是一个轻量级的控制反转( IoC )和面向切面编程( AOP )的框架。**

官方文档：https://docs.spring.io/spring-framework/docs/5.3.10-SNAPSHOT/reference/html/index.html

使用 Spring 需要的 Maven 依赖（通过这个依赖 Maven 会自动导入其前置 jar 包）

```xml
<!-- https://mvnrepository.com/artifact/org.springframework/spring-webmvc -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.3.9</version>
</dependency>
```

#### 1.2 Spring组成

Spring 由七个模块组成

![image-20210820170627574](F:\TyporaMD\Spring\Spring初见\image-20210820170627574.png)

了解一下知道是七个模块就行，具体的后面再研究。

### 2. IoC理论推导

这里创建 Spring-Study 项目作为后面的父项目，在父项目的 pom.xml 中添加 Spring 需要的依赖（就是上面那个），这样其子项目都不用再添加这个依赖了。

在父项目中创建一个 Spring-01-IoC1 项目实践一下 IoC 的理论推导是怎么来的。

#### 2.1 之前的方式

按照之前写 SMBMS 项目时的做法，假设服务器想要获取某个用户的的信息，需要编写 UserDao --> UserDaoImpl --> UserService --> UserServiceImpl --> Servlet 的代码，这里简单地回顾一下

UserDao 接口的代码

```java
public interface UserDao {
    // 要返回点什么，当个例子随便返回
    int getUser();
}
```

UserDaoImpl 实现类的代码，实现了其中的获取用户信息的方法

```java
public class UserDaoImpl implements UserDao{
    public int getUser() {
        System.out.println("查询用户信息");
        return 0;
    }
}
```

UserService 接口的代码

```java
public interface UserService {
    // 调用 Dao 层的方法
    int getUser();
}
```

UserService 实现类的代码，业务层调用 Dao 层，需要先引入 Dao 层

```java
public class UserServiceImpl implements UserService{
    // 之前的做法，要用谁的方法，就把谁放进来
    private UserDao userDao = new UserDaoImpl();

    public int getUser() {
        return userDao.getUser();
    }
}
```

写个测试方法，当做 Servlet 在调用

```java
public class MyTest {
    @Test
    public void Test(){
        UserService userService = new UserServiceImpl();
        // 查询到了东西
        int i = userService.getUser();
        System.out.println(i);
    }
}
// 测试方法执行结果：
// 查询用户信息
// 0
```

**看起来似乎没什么问题。**不过如果用户的需求变了，现在要到 MySql 中查询数据，那要怎么改呢？

首先要增加一个 UserDao 接口的实现类 UserDaoMysqlImpl

```java
public class UserDaoMysqlImpl implements UserDao{
    public int getUser() {
        System.out.println("Mysql查询用户信息");
        return 1;
    }
}
```

因为它实现了 UserDao 接口，所以可以用它查询数据；不过，为了使用它，必须修改 UserServiceImpl 中引入的对象（修改了原来的代码！问题已经很严重了）

```java
public class UserServiceImpl implements UserService{
    // 之前的做法，要用谁的方法，就把谁放进来
    //private UserDao userDao = new UserDaoImpl();
    private UserDao userDao = new UserDaoMysqlImpl();
    
    public int getUser() {
        return userDao.getUser();
    }
}
// 测试方法执行结果：
// Mysql查询用户信息
// 1
```

这样倒是可以使用了。**不过，如果又有了新的需求**，如要到 Oracle 中查询数据，就要建立 UserDaoOracleImpl 实现类

```java
public class UserDaoOracleImpl implements UserDao{
    public int getUser() {
        System.out.println("Oracle查询用户信息");
        return 2;
    }
}
```

要使用它的时候，同样需要修改 UserServiceImpl

```java
public class UserServiceImpl implements UserService{
    // 之前的做法，要用谁的方法，就把谁放进来
    //private UserDao userDao = new UserDaoImpl();
    //private UserDao userDao = new UserDaoMysqlImpl();
    private UserDao userDao = new UserDaoOracleImpl();

    public int getUser() {
        return userDao.getUser();
    }
}
// 测试方法执行结果：
// Oracle查询用户信息
// 2
```

说了这么多，其实总结起来就是一句话：**之前的程序，不符合开闭原则，耦合性太高了；如果代码量非常庞大，扩展功能的成本就非常昂贵。**

#### 2.2 控制反转后

**那要怎么修改之前的代码才好呢？**其实也很简单，即为 UserServiceImpl 实现类添加一个 set 方法，接收要使用的接口实现类

```java
public class UserServiceImpl implements UserService{
	
    private UserDao userDao;
    // 更好的方式，要用那个实现就自己传进来
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public int getUser() {
        return userDao.getUser();
    }
}
```

在测试方法中使用一下，想使用哪个接口，就设置为哪个接口

```java
public class MyTest {
    @Test
    public void Test(){
        UserService userService = new UserServiceImpl();
        // 由用户来选择要用什么（接口中没有这个方法，要强转为实现类）
        ((UserServiceImpl)userService).setUserDao(new UserDaoMysqlImpl());
        // 查询到了东西
        int i = userService.getUser();
        System.out.println(i);
    }
}
// 测试方法执行结果：
// Mysql查询用户信息
// 1
```

在这里只使用了一个 set 的方法，程序已经发生了革命性的变化。

在之前的业务中，用户的需求可能会改变原有的代码，如果代码量很大，改变代码的成本非常昂贵；添加了 set 方法后，对于新的需求，只需要添加其实现，通过这种方式注入就可以了。**不过这里并不是要说 set 方法多么好用，而是要理解这其中的思想：控制反转（ IoC ）。**

![image-20210820223222672](F:\TyporaMD\Spring\Spring初见\image-20210820223222672.png)

**⭐总结⭐**

- **之前的方式，程序主动创建对象，主动权在程序（程序员设置）；**

- **控制反转后，即使用 set 方法注入，程序不再有主动性，而是变成了被动的接收对象；**

- **这种思想从本质上解决了问题，我们不用再管理对象的创建了，程序的耦合性大大地降低了，这就是最简单的 IoC 思想。**

#### 2.3 IoC本质

**控制反转（ Inversion of Control，IoC ），是一种设计思想，依赖注入（ Dependency）是实现 IoC 的一种方法。**

> 如果一个类 A 的功能实现需要使用到类 B，则类 B 就是类 A 的依赖。如果在类 A 内部实例化类 B，则两者间的耦合度就非常高。
>
> 若类 B 出现问题，或要更换为类 C 去实现，则需要对整个类 A 进行修改。如果这样的问题非常多，那么代码维护就会非常困难且容易出现问题。
>
> 解决问题的方法是把类 A 对于类 B 的依赖剥离出来，交给第三方去实现。把控制权交给第三方，就是控制反转（ Inversion of Control，IoC ）。
>
> 控制反转是一种思想，是能够解决问题的一种方式，而依赖注入就是控制反转最典型的实现方法。由第三方（ IoC 容器）来控制依赖，将依赖通过属性、构造函数或工厂方法注入到需要此依赖的类中（如将类 B 注入到类 A 中），这样就能极大地对这两个类进行解耦。

**控制反转是一种通过描述（ XML 或注解）和第三方去生产或获取特定对象的方式，在 Spring 中实现控制反转的是 IoC 容器，实现的方法是依赖注入。**

### 3. 第一个Spring程序

#### 3.1 HelloSpring

新建 Spring-02-HelloSpring 项目，写一下第一个 Spring 程序。

第一步，写一个 Hello 类，这就是一个很普通的 JavaBean，只有一个参数 str

```java
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
```

第二步，配置 Spring 使其去管理对象，引用官方文档的格式

> 以下示例显示了基于 XML 的配置元数据的基本结构：
>
> ```xml
> <?xml version="1.0" encoding="UTF-8"?>
> <beans xmlns="http://www.springframework.org/schema/beans"
>     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
>     xsi:schemaLocation="http://www.springframework.org/schema/beans
>         https://www.springframework.org/schema/beans/spring-beans.xsd">
> 
>     <!--该id属性是一个字符串，用于标识单个 bean 定义-->
>     <!--该class属性定义 bean 的类型并使用完全限定的类名-->
>     <bean id="..." class="...">  
>         <!-- collaborators and configuration for this bean go here -->
>     </bean>
> 
>     <!-- more bean definitions go here -->
> 
> </beans>
> ```

在 resources 文件夹下创建 beans.xml 文件（官方命名为 applicationContext.xml，即应用容器；为了方便就先叫 beans.xml，反正没什么影响），将 hello 对象配置到其中

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--把对象交给 Spring 管理-->
    <bean id="hello" class="com.qiyuan.entity.Hello">
        <property name="str" value="Spring"/>
    </bean>

</beans>
```

第三步，对象已经交给 Spring 管理了，使用一下看看；因为是用 xml 配置的，所以要用到 ClassPathXmlApplicationContext，如果是注解配置的，则要用到 AnnotationConfigApplicationContext

```java
public class MyTest {
    @Test
    public void HelloSpring(){
        // 获取 Spring 容器，使用 xml 配置必经步骤！
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // 对象都被 Spring 管理了，要使用取出来即可，参数就是配置的 id
        Hello hello = (Hello) context.getBean("hello");
        // 使用一下对象！
        System.out.println(hello);
    }
}
// 输出：
// Hello{str='Spring'}
```

第一个 Spring 程序 HelloSpring 就完成了。

**注意：**可能是因为名字没对上，beans.xml 会提示 "Application context not configured for this file"，这个问题不影响程序运行，但会导致被托管的 Bean 没有小叶子提示。

**解决方法：**打开 File ---> Project Structure ---> Modules，选择该项目下的 Spring（应该会有），点加号将 beans.xml 设置为 Spring Application Context 即可。

这样在 Hello 类的旁边就可以看到一个绿叶，表明该类已由 Spring 接管，同时设置了属性的 set 方法旁边可以看到 P 的标志，表明该属性被设置了。

**思考问题**

- Q：这个 hello 对象是谁创建的？

  A：hello 对象是由 Spring 创建的。

- Q：这个 hello 对象的属性是谁设置的？

  A：hello 对象的属性是由 Spring 设置的。

**这个过程就是控制反转：IoC 是一种编程思想，程序对象由主动地编程创建变成了被动地接受。**

- **控制：原来由程序控制对象的创建，现在把由 Spring 控制对象创建，控制权在 Spring。**

- **反转：程序本身不创建对象，而是被动地接受对象。**

- **依赖注入：在这里就是用 set 方法进行注入的。**

**到了现在，修改程序就可以不用到程序中改动了，只需要在 xml 配置文件中修改就行了，即对象由 Spring 来创建、管理、装配！**

#### 3.2 修改上个程序

把上一个程序 Spring-01-IoC1 也修改为使用 Spring 框架，添加 beans.xml 配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!--把对象交给 Spring 管理-->
    <bean id="DefaultImpl" class="com.qiyuan.dao.UserDaoImpl"/>
    <bean id="MysqlImpl" class="com.qiyuan.dao.UserDaoMysqlImpl"/>
    <bean id="OracleImpl" class="com.qiyuan.dao.UserDaoOracleImpl"/>

    <bean id="userService" class="com.qiyuan.service.UserServiceImpl">
        <!-- value: 基本数据类型 || ref: 引用 Spring 中管理的对象 -->
        <property name="userDao" ref="DefaultImpl"/>
    </bean>

</beans>
```

在配置中将 DefaultImpl 注入到 userService 中，测试方法如下

```java
public class MyTest {
    @Test
    public void Test(){
        // 获取容器
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // 获取对象
        UserService userService = (UserService) context.getBean("userService");
        // 查询到了东西
        int i = userService.getUser();
        System.out.println(i);
    }
}
// 执行结果：
// 查询用户信息
// 0
```

想要更换实现，只需要修改 beans.xml 中 userService 对象的依赖，即 ref="DefaultImpl" 为其他的即可，而不用修改程序！试想修改程序还要重新编译呢，修改配置文件只要读取文件就行了！

### 4. IoC创建对象方式

IoC 容器是在获取 context 时创建所有配置好的对象的，那又是怎么创建对象的呢？上面提到，可以通过属性、构造函数或工厂方法模式注入依赖。这里用 Spring-03-IoC2 项目了解一下 IoC 创建对象的方式。

#### 4.1 无参构造创建

默认情况下，一个 JavaBean 有属性及对应属性的 set 方法，且每个类都有一个隐式的无参构造器（如果不用有参构造覆盖的话），在这种情况下，IoC 容器创建对象的步骤为

1. 用无参构造器创建对象
2. 通过 set 方法设置属性
3. 将对象放到IoC 容器中，由 IoC 容器管理对象

在上面的 beans.xml 中都是这么做的，这里就不再重复试验了。

#### 4.2 有参构造创建

首先创建一个 User 实体类，同时写一个有参构造器

```java
public class User {
    private String name;
    
    public User(String name) {
        this.name = name;
    }
    
	...
}
```

现在通过这个有参构造器来设置其属性（如果属性是其他类，就是依赖注入了），**注意：**有了有参构造器后，隐式的无参构造器就没有了，此时再通过之前的方式创建对象就会报错，需要手动补充一个显式的无参构造器。

**第一种方式**，通过有参构造器参数的下标设置

```xml
    <!--通过有参构造器参数的下标设置-->
    <bean id="user" class="com.qiyuan.entity.User">
        <constructor-arg index="0" value="Qiyuanc"/>
    </bean>
```

**第二种方式**，通过有参构造器参数的类型设置

```xml
    <!--通过有参构造器参数的类型设置-->
    <bean id="user2" class="com.qiyuan.entity.User">
        <constructor-arg type="java.lang.String" value="Qiyuanc"/>
    </bean>
```

注意，如果有两个参数类型相同，则会按照参数顺序进行设置。总之最好不要使用这种方式。

**第三种方式**，通过有参构造器参数的名称设置，和之前的 property 相同，也是最好用的方式

```xml
    <!--通过有参构造器参数的名称设置-->
    <bean id="user3" class="com.qiyuan.entity.User">
        <constructor-arg name="name" value="Qiyuanc2"/>
    </bean>
```

如果要在有参构造器中注入其他类的对象，同样也是使用 ref 进行设置，这里也不试验了。

**强调：**IoC 容器是在获取 context 时创建所有配置好的对象的，无论某个对象是否被获取，它都会被创建；同时，配置的每个对象，在 IoC 容器中有且只有一份实例，即通过 getBean 获取一个对象两次，它们在内存中的地址是相同的（用 == 比较为 true ）。

### 5. 总结

**Spring是一个轻量级的控制反转( IoC )和面向切面编程( AOP )的框架。**

**控制反转（ Inversion of Control，IoC ），是一种设计思想，依赖注入（ Dependency Injection ）是实现 IoC 的一种方法。**

- **控制：原来由程序控制对象的创建，现在把由 Spring 控制对象创建，控制权在 Spring。**

- **反转：程序本身不创建对象，而是被动地接受对象。**

- **依赖注入：可以将依赖通过属性、构造方法或工厂方法模式注入。**

刚开始学 Spring，主要的概念理解的还不够透彻，一直在重复重复😵。
