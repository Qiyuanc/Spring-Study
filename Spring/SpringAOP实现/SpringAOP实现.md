## SpringAOP实现

说完了代理模式，就可以研究一下 Spring AOP 了。AOP 不是新的技术，而是对现有技术的更好的使用的方式，其实就是代理模式的典型应用。这一节新建 Spring-09-AOP 项目学习 Spring AOP。

### 1. AOP简介

#### 1.1 什么是AOP

AOP 即 Aspect Oriented Programming，意为**面向切面编程**，通过**预编译方式**和**运行期间动态代理**实现程序功能的统一维护的一种技术。AOP 是 OOP 的延续，是软件开发中的一个热点，也是 Spring 框架中的一个重要内容，是函数式编程的一种衍生范型。利用 AOP 可以对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。

举个例子，如下图，加减乘除四个业务，虽然它们的业务逻辑不同，但他们都有一样的验证参数和日志功能，就可以横向地把这些功能提取出来（抽取横切关注点），以组合的方式添加到不同的业务逻辑上（代理模式！增强方法！），这样避免了修改原有的业务逻辑，使类的功能更明确，程序的粒度更细！

![image-20210826170056433](F:\TyporaMD\Spring\SpringAOP实现\image-20210826170056433.png)

#### 1.2 AOP术语

AOP 的特性术语，不同的翻译还会不一样，得在过程中理解

- **横切关注点**：跨越程序多个模块的方法或功能。即与业务逻辑无关，但我们也要关注的部分，就是横切关注点。如日志、安全、缓存等。
- **切面（ Aspect ）**：横切关注点被模块化的特殊对象；即切面应是一个类。
- **通知（ Advice）**：切面要完成的增强处理，通知描述了切面何时执行以及如何执行增强处理；即通知应是切面中的方法。
- **目标（ Target ）**：被通知对象。
- **代理（ Proxy ）**：向目标对象应用通知之后创建的对象。
- **切入点（ PointCut ）**：切面通知执行的地点，即可以插入增强处理的连接点。
- **连接点（ JoinPoint ）**：应用执行过程中能够插入切面的一个点，这个点可以是方法的调用、异常的抛出。
- **织入（ Weaving ）**：将增强处理添加到目标对象中，并创建一个被增强的对象，这个过程就是织入。

举个例子，现在要在程序中多处添加日志，则日志就是**横切关注点**，把日志模块化为 Log 类，就是**切面**，日志要输出的内容就是**通知**，**目标**就是要添加日志的接口或方法，**代理**就是动态生成的日志代理，**切入点**即执行输出日志的地点，**连接点**就是动态代理中的 invoke 方法前后，可以插入切面（ Before/After ）。

#### 1.3 Spring中的通知

Spring中有五种通知 Advice

- @Before 前置通知：方法执行之前
- @After 后置通知：方法执行之后
- @Around 环绕通知：方法执行前后都有通知
- @AfterReturnning 返回通知：方法成功执行之后通知
- @AfterThrowing 异常通知：抛出异常之后通知

就像代理模式中 Before 和 After 时的增强，不过功能更多了。

### 2. 使用Spring AOP

在 Spring 中使用 AOP 要先导入 AspectJ 的包

```xml
<!-- https://mvnrepository.com/artifact/org.aspectj/aspectjweaver -->
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.7</version>
    <!--骇人鲸！-->
    <scope>runtime</scope>
</dependency>
```

创建 UserService 接口和 UserServiceImpl 实现类，还是增删改查四个功能

```java
public interface UserService {
    void add();
    void delete();
    void update();
    void select();
}
```

```java
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

    public void select() {
        System.out.println("查询用户！");
    }
}
```

#### 2.1 使用Spring的接口

现在要给业务添加日志，抽取为切面，就要创建 Log 类了，不过这里根据前置日志和后置日志，又分为 BeforeLog 类和 AfterLog 类

BeforeLog 类，实现了 MethodBeforeAdvice 接口，即前置通知

```java
public class BeforeLog implements MethodBeforeAdvice {
    // method: 要执行的目标对象的方法 | the method being invoked
    // args: 方法的参数 | the arguments to the method
    // target: 目标对象 | the target of the method invocation
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(target.getClass().getName()+" 的 "+method.getName()+" 方法被执行了！");
    }
}
```

AfterLog 类，实现了 AfterReturningAdvice 接口，即返回通知

```java
public class AfterLog implements AfterReturningAdvice {
    // 多了一个返回值的参数
    // returnValue: 方法调用的返回值 | the value returned by the method, if any
    // 其他参数都是一样的！
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("执行了 "+method.getName()+" 方法，返回值为 "+returnValue);
    }
}
```

然后就可以用 Spring 把切面切入进去了！

创建 applicationContext.xml 配置文件，**引入 AOP 的约束**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans ...
       // AOP 约束
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="...
        // AOP 约束
        http://www.springframework.org/schema/aop 
        https://www.springframework.org/schema/aop/spring-aop.xsd">
    
</beans>
```

然后注册一下 bean

```xml
    <bean id="userService" class="com.qiyuan.service.UserServiceImpl"/>
    <bean id="beforeLog" class="com.qiyuan.log.BeforeLog"/>
    <bean id="afterLog" class="com.qiyuan.log.AfterLog"/>
```

开启 Spring AOP 配置，设置切入点和通知，**这里用到了 execution 表达式表明切入点的位置，而通知执行的位置是由实现的接口决定的**

```xml
    <!--开启 AOP 配置-->
    <aop:config>
        <!--声明一个切入点，expression 是一个 execution 表达式，表明切入点在哪-->
        <!-- execution 表达式是什么几把玩意后面再说！先用着！这里表明切入点是 UserServiceImpl 下所有方法-->
        <aop:pointcut id="logpointcut" expression="execution(* com.qiyuan.service.UserServiceImpl.*(..))"/>
        <!--执行环绕增加，增强方法-->
        <!-- Spring根据实现的接口 决定通知的连接点-->
        <!-- beforelog 实现的是 前置通知 接口，所以连接点在切入点方法执行前-->
        <aop:advisor advice-ref="beforeLog" pointcut-ref="logpointcut"/>
        <!-- afterlog 实现的是 返回通知 接口，所以连接点在切入点方法执行成功后-->
        <aop:advisor advice-ref="afterLog" pointcut-ref="logpointcut"/>
    </aop:config>
```

设置完后，IDEA 也会显示通知会在哪个方法执行

![image-20210826184101745](F:\TyporaMD\Spring\SpringAOP实现\image-20210826184101745.png)

现在就可以看一下效果了，用测试方法测试一下

```java
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        // 因为动态代理代理的是接口！所以获取对象要用接口接收
        UserService userService = context.getBean("userService", UserService.class);
        userService.add();
        userService.delete();
    }
}
// 执行结果
/*
    com.qiyuan.service.UserServiceImpl 的 add 方法被执行了！
    增加用户！
    执行了 add 方法，返回值为 null
    com.qiyuan.service.UserServiceImpl 的 delete 方法被执行了！
    删除用户！
    执行了 delete 方法，返回值为 null
*/
```

**现在回头看看，UserService 接口及其实现类只用在乎它的本职工作，Log 类也只用专注于输出日志，他们看似毫无关联，但我们通过切面编程的方式，把 Log 切入到了 UserService 中去！这，就是 Spring AOP。**

**注意**：在获取对象时，实现了接口的对象要用接口去获取 bean，如

```java
UserService userService = context.getBean("userService", UserService.class);
```

这里如果改成

```java
UserService userService = context.getBean("userService", UserServiceImpl.class);
```

会出现错误为

```java
org.springframework.beans.factory.BeanNotOfRequiredTypeException: 
Bean named 'userService' is expected to be of type 'com.qiyuan.service.UserServiceImpl' but was actually of type 'com.sun.proxy.$Proxy6'
```

**这是因为动态代理中，代理的是一个接口（ UserService ），这里返回的是由 Spring 创建的一个代理类，它也实现了代理的接口（ UserService ），所以可以用接口接收；但一个接口的实现类不能接受其另一个实现类（如 UserServiceImpl1 不能接受 UserServiceImpl2 ），就是这么简单的道理。**

#### 2.2 使用自定义类（切面）

除了在 XML 中配置实现了 Spring 提供的接口的类的方法，使用自定义的类也能使用 Spring AOP。

首先创建一个 MyAOP 类（就是切面），类中有需要切入的方法（通知）

```java
// 这就是一个切面！
public class MyAOP {
    
    public void Before(){
        System.out.println("=====方法执行前=====");
    }
    
    public void After(){
        System.out.println("=====方法执行后=====");
    }
}
```

 在 applicationContext.xml 中进行 AOP 配置

```xml
    <!--自定义的切面-->
    <bean id="myaop" class="com.qiyuan.aop.MyAOP"/>

	<aop:config>
        <!--引用自定义切面-->
        <aop:aspect id="myaop" ref="myaop">
            <!--设置切入点，和之前相同！-->
            <aop:pointcut id="pointcut" expression="execution(* com.qiyuan.service.UserServiceImpl.*(..))"/>
            <!--在切入点的方法执行前，执行切面中的 Before 方法-->
            <aop:before method="Before" pointcut-ref="pointcut"/>
            <!--在切入点的方法执行后，执行切面中的 After 方法-->
            <aop:after method="After" pointcut-ref="pointcut"/>
        </aop:aspect>
    </aop:config>
```

这种方式比第一种更简洁明了，**创建了一个切面的 bean，和之前一样设置切入点，然后配置在什么时候（ aop:before、aop:after、aop:around... ）执行切面（ pointcut-ref ）中的什么方法（ method= ）。**不过功能也没有第一种强大，获取不到类的名字，方法的名字等。

运行测试方法看看效果

```java
// 和之前一样的测试方法

// 执行结果
/*
    =====方法执行前=====
    增加用户！
    =====方法执行后=====
    =====方法执行前=====
    删除用户！
    =====方法执行后=====
*/
```

#### 2.3 小结

**两种方法的共同点**

- 都要声明切入点 pointcut，表明在什么地方进行切入

**两种方法的不同点**

- 使用 Spring 的接口，切面实现了什么接口就在什么地方进行通知（执行方法），如实现了 MethodBeforeAdvice 接口，Spring 就会把通知切入到切入点的方法执行前。
- 使用自定义类（切面），要手动设置将切面中的方法（通知）设置到什么时候执行，如用 aop:before 在切入点方法执行前执行 method="Before" 方法。

#### 2.4 使用注解实现

使用注解和使用自定义类差不多，就是用注解换掉了 XML 中的配置。

创建 AnnotationAspect 类，**使用 @Aspect 声明这个类是一个切面**

```java
// 声明这个类是一个切面！
@Aspect
public class AnnotationAspect {
}
```

将这个类声明为切面，相当于 XML 中的

```xml
<aop:aspect id="myaop" ref="myaop">
```

**注意**：这里给 Maven 害惨了，之前导入的 Maven 依赖中设置了作用域

```xml
	<!--骇人鲸！-->
    <scope>runtime</scope>
```

**这个配置导致 @Aspect 注解怎么都找不到！把它去掉就好了！**修改完的依赖为

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.7</version>
</dependency>
```

给这个类添加方法，即切面对应的通知，和之前一样

```java
// 声明这个类是一个切面！
@Aspect
public class AnnotationAspect {

    public void Before(){
        System.out.println("=====方法执行前=====");
    }

    public void After(){
        System.out.println("=====方法执行后=====");
    }
    
}
```

这时候还没完哦，**使用 @Before 和 @After 给这两个方法（通知）设置切入点**

```java
@Aspect
public class AnnotationAspect {

    // 和 XML 中一样，通过 execution 表达式设置切入点
    @Before("execution(* com.qiyuan.service.UserServiceImpl.*(..))")
    public void Before(){
        System.out.println("=====方法执行前=====");
    }
    @After("execution(* com.qiyuan.service.UserServiceImpl.*(..))")
    public void After(){
        System.out.println("=====方法执行后=====");
    }

}
```

这里就相当于 XML 中的

```xml
<!--设置切入点，和之前相同！-->
<aop:pointcut id="pointcut" expression="execution(* com.qiyuan.service.UserServiceImpl.*(..))"/>
<!--在切入点的方法执行前，执行切面中的 Before 方法-->
<aop:before method="Before" pointcut-ref="pointcut"/>
<!--在切入点的方法执行后，执行切面中的 After 方法-->
<aop:after method="After" pointcut-ref="pointcut"/>
```

这时候还没完！**还要再 XML 中把这个切面注册一下，并且开启注解支持**

```xml
<!--使用注解-->
<bean id="annoAspect" class="com.qiyuan.aop.AnnotationAspect"/>
<!--开启注解支持-->
<aop:aspectj-autoproxy/>
```

这样才是配置好了，运行测试方法，结果和第二种方式一样，就不贴出来了。

这里还有个环绕方法 @Around 没试，就先这样吧。

### 3. 总结

理解了动态代理，AOP 学起来就比较轻松了。

**AOP 的作用在于：不影响现有业务的情况下，添加一些程序中普遍会用到的功能，如日志。**

使用 AOP 的方式有三种，Spring 接口、自定义切面和注解。注解还用的不是很明白，不过先这样吧~🥴。