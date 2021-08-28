## SpringBean自动装配

自动装配是 Spring 满足 bean 依赖的一种方式，之前都要手动给 bean 注入依赖，否则属性就为空；而使用自动装配，Spring 就会在容器自动寻找需要的依赖，并装配到 bean 中。

新建 Spring-05-Autowired 项目使用一下 Spring 的自动装配。

### 1. 测试环境搭建

在 Spring-05-Autowired 项目中创建 Person 类、Cat 类、Dog 类，类之间的关系为一个人有两个宠物猫和狗（为了方便就不创建包了，直接在 main/java 下建类）

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person {
    private String name;
    private Cat cat;
    private Dog dog;
}
```

```java
public class Cat {
    public void shout(){
        System.out.println("喵喵喵");
    }
}
```

```java
public class Dog {
    public void shout(){
        System.out.println("汪汪汪");
    }
}
```

再创建 beans.xml 配置文件，按之前的方式创建一个 Person 对象

```xml
<!--没有属性需要设置-->
<bean id="cat" class="Cat"/>
<bean id="dog" class="Dog"/>

<!--用一下 p 命名空间！-->
<bean id="person" class="Person" p:name="祈鸢" p:cat-ref="cat" p:dog-ref="dog"/>
```

搞完了，写个测试方法让宠物叫一下

```java
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Person person = context.getBean("person", Person.class);
        person.getCat().shout();
        person.getDog().shout();
    }
}
// 执行结果
/*
    喵喵喵
    汪汪汪
*/
```

之前手动装配就是这样做的，下面用一下自动装配。

### 2. XML自动装配

使用自动装配，要在 bean 标签内配置 autowire 属性， autowire 属性有 byName、byType、constructor、default、no 几种属性

#### 2.1 byName

使用 byName 方式自动装配，**Spring 会自动在容器中通过 set 方法后的字段（如 setCat，字段是 Cat，就去寻找 cat 对象）寻找 Person 类缺少的依赖**，这里缺少的是 Cat 类和 Dog 类，就会去寻找上面的 cat 和 dog

```xml
<bean id="person" class="Person" p:name="祈鸢" autowire="byName"/>
```

运行上面的测试方法，结果一样。

**注意**：**使用 byName 自动装配，对 bean 的 id 有要求**。如上面的 id 的 cat 如果变成 cat1，则 Spring 就找不到这个对象了；而且要注意大小写，如 cat 如果 写成 Cat，也会让 Spring 找不到。自动装配找不到需要的对象，依赖就会变成 null 了。

#### 2.2 byType

使用 byType 方式自动装配，Spring 会自动在容器中寻找依赖所需的类型对应的 bean，如 person 需要的依赖是 Cat 类型和 Dog类型，就会去寻找这两种类型的对象。由于只和类型相关，所以对应的 bean 甚至可以没有 id

```xml
<bean class="Cat"/>
<bean class="Dog"/>

<bean id="person" class="Person" p:name="祈鸢" autowire="byType"/>
```

再运行一下测试方法，结果也是一样的。

**注意**：既然是通过类型寻找目标的，那么问题也很明显，就是不能有多个同一类型的 bean！如不能同时有 cat 和 cat1，否则 IDEA 都会直接提示这个 bean 有问题，**必须保证需要的依赖类型全局唯一**。

### 3. 注解自动装配

现在就要开始了解一下 Spring 中非常多的注解了！

使用注解需要在配置文件头部配置注解的支持和导入约束

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    
    <context:annotation-config/>

</beans>
```

#### 3.1 @Autowired

在属性上添加 @Autowired 注解可以对该属性实现自动装配（加在 set 方法上也行）

```java
...
public class Person {
    private String name;
    @Autowired
    private Cat cat;
    @Autowired
    private Dog dog;
}
```

使用了 @Autowired 后，在装配时

1. 先按照 byType 去寻找该属性对应的类型，如果只有一个该类型对象则直接装配
2. 如果有多个该类型的对象，再按照 byName 去寻找命名为类名的对象（如 Cat 类型对应的 cat 对象）进行装配
3. 如果两种方式都寻找失败，则装配失败，属性为 null

```xml
<!--使用注解自动装配-->
<!--对于 cat，使用的是 byName-->
<bean id="cat" class="Cat"/>
<bean id="cat2" class="Cat"/>
<!--对于 dog，使用的是 byType-->
<bean id="dog" class="Dog"/>
<bean id="person" class="Person" p:name="祈鸢"/>
```

另外，使用 @Autowired 可以不用编写 set 方法，也能注入依赖！

**扩展一下**，点进 @Autowired 的接口中看一下，有一个 required 属性，默认为 true

```java
public @interface Autowired {
    boolean required() default true;
}
```

required 属性默认设置为 ture 的作用为限制添加了 @Autowired 注解的属性不能为空，即找不到要装配的对象时会报错；若将其设置为 false，则找不到要装配的对象程序也不会报错

如在 Dog 属性上设置为 false，同时删掉 beans.xml 配置的 dog 对象

```java
...
public class Person {
    private String name;
    @Autowired
    private Cat cat;
    @Autowired(required = false)
    private Dog dog;
}
```

执行测试方法，结果为

```java
// 执行结果

// 喵喵喵
// java.lang.NullPointerException
// ...
```

这里的空指针异常是因为调用了 getDog().shout() 方法，由于允许为空但又不存在符合的对象，所以这里的 getDog() 获取为空。

如果不设置 required = false，且删掉 beans.xml 配置的 dog 对象，执行测试方法会报错为

```java
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type 'Dog' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)}
```

即由于 required = true，但又找不到对象进行装配的错误（ expected at least 1 bean ）。

#### 3.2 @Qualifier

如果 @Autowired 使用的环境比较复杂，可以和 @Qualifier 配合使用。 @Qualifier 中只有一个属性 value，设置后自动装配会直接寻找对应 id 的对象，如果找不到 IDEA 都会直接标红。

**注意**：配置 @Qualifier 后 @Autowired 只会去查找指定的对象，会屏蔽掉 byType 和 byName 的自动装配。

在 beans.xml 中添加两只猫和两只狗，避免 byType 装配，同时 id 后带上1和2，避免 byName 装配

```xml
<!-- @Qualifier寻找对象-->
<bean id="cat1" class="Cat"/>
<bean id="cat2" class="Cat"/>
<bean id="dog1" class="Dog"/>
<bean id="dog2" class="Dog"/>
<bean id="person" class="Person" p:name="祈鸢"/>
```

通过 @Qualifier 注解指定特定的对象

```java
...
public class Person {
    private String name;
    @Autowired
    @Qualifier(value = "cat2")
    private Cat cat;
    @Autowired
    @Qualifier(value = "dog2")
    private Dog dog;
}
```

执行测试方法，结果为

```java
// 执行结果
/*
    喵喵喵
    汪汪汪
*/
```

说明找到对象了，装配成功了！

#### 3.3 @Resource

@Resource 注解是 JAVA 自带的注解，功能上是 @Autowired 和 @Qualifier 的集合体。

即 @Resource 也能根据 byType 和 byName 来自动装配，这里和 @Autowired 相同；同时  @Resource 也具有 name 属性，可以指定要装配的对象，这里和 @Qualifier 相同

```java
...
public class Person {
    private String name;
    @Resource(name = "cat2")
    private Cat cat;
    @Resource(name = "dog2")
    private Dog dog;
}
```

运行测试方法没有问题，这里就省略了。

### 4. 总结

本节了解了如何自动装配 bean

- 使用 xml，在 bean 标签中要设置 autowired 的属性为 byType 或 byName
- 使用注解，一般来说 @Autowired 也够用了，再不行就结合 @Qualifier 或功能强大的 @Resource

总体而言使用注解比较简单，但要装配的对象一旦多起来，就会很乱😵。