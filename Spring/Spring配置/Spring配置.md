## Spring配置

本节仍用 Spring-03-IoC2 项目了解一下在 Spring 的 xml 中的一些配置。比起 MyBatis，Spring 的配置可以说是非常少了。

### 1. alias 别名

可以使用 alias 标签给对象设置别名

```xml
<!--通过有参构造器参数的名称设置-->
<bean id="user" class="com.qiyuan.entity.User">
    <constructor-arg name="name" value="Qiyuanc3"/>
</bean>

<!--给对象设置个别名，通过这个别名也可以获取到对象-->
<alias name="user" alias="qiyuan"/>
```

### 2. bean 配置

之前用到了 bean 的 id，即这个对象的标识符；和 class，即这个对象的类，除了这两个标签，bean 还可以配置 name 属性给对象设置别名，如

```xml
<bean id="user" class="com.qiyuan.entity.User" name="qiyuanUser">
    <constructor-arg name="name" value="Qiyuanc3"/>
</bean>

<!-- User user = (User) context.getBean("qiyuanUser"); -->
```

而且在使用 name 属性设置多个别名的时候，支持多种分隔符如空格、逗号、分号

```xml
<bean id="user" class="com.qiyuan.entity.User" name="qiyuanUser qiyuanUser2,qiyuanUser3;qiyuanUser4">
    <constructor-arg name="name" value="Qiyuanc3"/>
</bean>

<!-- User user = (User) context.getBean("qiyuanUser4"); -->
```

既然如此，我为什么要使用 alias 设置别名呢···而且取 id 的时候直接设置好不就好了嘛？

### 3. import 导入

假设项目中有多个人进行开发，每个人负责不同的类的开发，不同的类的对象就要放在不同的 xml 文件中，最后怎么组合起来呢？这就要用到 import 标签。

 import 标签是个很有用的标签，一般在团队开发中使用。它的功能是在一个 xml 文件中导入其他 xml 文件，**将多个 xml 文件集合成一个**，使用的时候加载总的配置文件就行了。

首先创建一个 beans2.xml 文件，在里面放入一个对象

```xml
<!--通过有参构造器参数的名称设置-->
<bean id="beans2User" class="com.qiyuan.entity.User">
    <constructor-arg name="name" value="这个是 beans2 的对象！"/>
</bean>
```

再创建一个 applicationContext.xml 配置文件（这是最正规的配置文件名称），导入 beans.xml 和 beans2.xml

```xml
<import resource="beans.xml"/>
<import resource="beans2.xml"/>
```

然后在测试方法中读取 applicationContext.xml 配置文件，获取一下 beans.xml 和 beans2.xml 中的对象

```java
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        User user = (User) context.getBean("qiyuan");
        User beans2User = (User) context.getBean("beans2User");
        System.out.println(user);
        System.out.println(beans2User);
    }
}
// 执行结果
// User{name='Qiyuanc3'}
// User{name='这个是 beans2 的对象！'}
```

可以看到，只导入了一个配置文件，能获取到两个配置文件中的对象。

**注意**：如果导入的配置文件中的对象重名了，则根据 import 的顺序，后来的会覆盖前面的。

### 4. 总结

Spring 的配置还是挺少的，目前会用到的也就三个吧，而且主要还是配置 bean。少到我已经要瞎写点东西当总结了😥。