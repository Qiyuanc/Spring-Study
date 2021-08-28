## Spring依赖注入

本节新建 Spring-04-DI 项目了解一下 Spring 的依赖注入。

**依赖注入**：依赖即某个类中的属性，注入即由 Spring 进行属性的设置。

### 1. 测试环境搭建

在 Spring-04-DI 项目中建立 Address 类和 Student 类作为测试对象，其中 Student 类中有基本类型、其他类的对象、数组、List、Map、Set、空值、Properties 的属性，使用 Lombok 为其添加 get/set 方法

```java
@Data
@ToString
public class Address {
    private String address;
}
```

```java
@Data
@ToString
public class Student {
    // 基本类型
    private String name;
    // 其他类的对象
    private Address address;
    // 数组
    private String[] books;
    // List
    private List<String> hobbies;
    // Map
    private Map<String,String> card;
    // Set
    private Set<String> games;
    // 空值
    private String Money;
    // 配置文件属性
    private Properties info;
}
```

在 resources 下创建 beans.xml 文件，添加一个 bean，并设置基本类型的属性值（就像之前那样）

```xml
<bean id="student" class="Student">
    <!--基本类型注入，value-->
    <property name="name" value="祈鸢"/>
</bean>
```

写个测试方法获取一下对象，输出里面的属性值

```java
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Student student = (Student) context.getBean("student");
        System.out.println(student.getName());
        System.out.println(student.getAddress());
    }
}
// 执行结果
// 祈鸢
// null
```

地址为 null 是肯定的，因为还没有注入这个依赖。接下来就了解一下 Student 类中复杂的类型要怎么注入依赖。

### 2. 依赖注入不同类型

这里使用的是 set 注入方式，即被注入的属性必须有其对应的 set 方法。

#### 2.1 bean注入

bean 注入即将其他类的对象注入到需要的对象中，之前也使用过，即

```xml
<bean id="address" class="Address">
    <property name="address" value="家"/>
</bean>

<bean id="student" class="Student">
    <!--基本类型注入，value-->
    <property name="name" value="祈鸢"/>
    <!-- bean 注入，ref-->
    <property name="address" ref="address"/>
</bean>
```

#### 2.2 数组注入

数组注入需要在要注入的属性中使用 array 标签，表明这个属性（依赖）是个数组，在数组中再使用 value（基本类型）或者 ref （自定义类型）逐个添加

```xml
<bean id="student" class="Student">
    ...
    <!--数组注入，array + value/ref -->
    <property name="books">
        <array>
            <!--如果是其他类型，就使用 ref-->
            <value>三体</value>
            <value>龙族</value>
            <!--<ref></ref>-->
        </array>
    </property>
</bean>
```

#### 2.3 List注入

List 注入与数组注入大同小异，即 array 标签换成了 list 标签

```xml
<bean id="student" class="Student">
    ...
    <!-- List 注入，list + value/ref -->
    <property name="hobbies">
        <list>
            <value>写作业</value>
            <value>玩游戏</value>
            <!--<ref></ref>-->
        </list>
    </property>
</bean>
```

#### 2.4 Map注入

Map 注入就不一样了，由于 Map 的键值对中键和值的类型都是 Object，即任意类型，所以注入时对基本类型使用 key 或 value，如果是其他类型，就要使用 key-ref 和 value-ref

```xml
<bean id="student" class="Student">
    ...
    <!-- Map 注入，map + entry key/value -->
    <property name="card">
        <map>
            <entry key="学号" value="0314"/>
            <entry key="身份证" value="03140723"/>
            <!--还有标签属性为-->
            <!--<entry key-ref="" value-ref=""/>-->
        </map>
    </property>
</bean>
```

#### 2.5 Set注入

Set 和 List 差不多，所以注入方式也差不多（ Set 中不能有重复元素，List 可以）

```xml
<bean id="student" class="Student">
    ...
    <!-- Set 注入，set + value/ref -->
    <property name="games">
        <set>
            <value>LOL</value>
            <value>APEX</value>
            <!--<ref></ref>-->
        </set>
    </property>
</bean>
```

#### 2.6 空值注入

通过 null 标签将某属性设置为 null，和空串注入不一样！

```xml
<bean id="student" class="Student">
	...
    <!--空值注入-->
    <property name="money">
        <null/>
    </property>
    <!--空串注入-->
    <!--<property name="money" value=""/>-->
</bean>
```

#### 2.7 Properties注入

properties 文件就是 key = value 的格式，在 props 标签内配置每个 prop 的 key 和 value 就行了

```xml
<bean id="student" class="Student">
	...
    <!-- properties 注入 key = value-->
    <property name="info">
        <!-- value 在尖括号外！-->
        <props>
            <prop key="学号">0723</prop>
            <prop key="性别">女</prop>
            <prop key="姓名">小祈</prop>
        </props>
    </property>
</bean>
```

#### 2.8 运行测试

终于把这么多不同类型的属性注入完了，现在运行测试方法看看结果

```java
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Student student = (Student) context.getBean("student");
        System.out.println(student.toString());
    }
}
// 执行结果
/* 
	Student(name=祈鸢, address=Address(address=家), 
	books=[三体, 龙族], hobbies=[写作业, 玩游戏], 
	card={学号=0314, 身份证=03140723}, games=[LOL, APEX], 
	Money=null, info={学号=0723, 性别=女, 姓名=小祈})
*/
```

可以看到不同类型的属性都注入成功了！

### 3. 命名空间注入

使用命名空间注入，可以简化一点配置，命名空间有 p 命名空间（对应 property ）和 c 命名空间（对应 constructor-arg ）。

另外创建一个实体类 User 来用一下命名空间注入

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private String name;
    private int age;
}
```

同时创建一个对应的 userbeans.xml 配置文件。

#### 3.1 P命名空间

p 命名空间简化了 property  的注入配置（一点），使用 p 命名空间，首先要在配置文件头中引入约束

```xml
<beans ...
       xmlns:p="http://www.springframework.org/schema/p"
       ...>
    
</beans>
```

配置一个 User 对象 user（如果需要引用，就使用如 p:address-ref，c 命名空间也是这样的）

```xml
<bean id="user" class="User" p:name="祈鸢" p:age="18"/>
<!--<bean id="user" class="User" p:name-ref=""/>-->
```

非常简单，就是简化了 property 标签！

#### 3.2 C命名空间

c 命名空间简化了 constructor-arg 的注入配置，使用 c 命名空间，首先要在配置文件头中引入约束

```xml
<beans ...
       xmlns:c="http://www.springframework.org/schema/c"
       ...>
    
</beans>
```

配置一个 User 对象 user2，使用 c 命名空间，本质上还是构造器注入，所以必须要有对应的有参构造器！

```xml
<bean id="user2" class="User" c:name="小祈" c:age="18"/>
<!--<bean id="user2" class="User" c:name-ref=""/>-->
```

也可以通过对应有参构造器参数的下标注入，再配置一个 user3

```xml
<bean id="user3" class="User" c:_0="小小祈" c:_1="17"/>
<!--<bean id="user2" class="User" c:_0-ref=""/>-->
```

也没什么好说的，其实就是简化了构造器注入的配置！

还是测试一下吧！运行测试方法

```java
public class MyTest {
    @Test
    public void Test2(){
        ApplicationContext context = new ClassPathXmlApplicationContext("userbeans.xml");
        // 反射！就知道是什么类了！
        User user = context.getBean("user", User.class);
        User user2 = context.getBean("user2", User.class);
        User user3 = context.getBean("user3", User.class);
        System.out.println(user);
        System.out.println(user2);
        System.out.println(user3);
    }
}
// 执行结果
/*
    User(name=祈鸢, age=18)
    User(name=小祈, age=18)
    User(name=小小祈, age=17)
*/
```

**注意**：之前获取对象都需要强转，因为 Java 不知道获取的对象是什么类型；这里使用**反射**获取，Java 就知道类型了，故不需要强转！

### 4. 总结

本节了解了一下 Spring 配 bean 的方式，其实就是官方文档的 1.4.1 和1.4.2 部分！

官方文档：https://docs.spring.io/spring-framework/docs/5.3.10-SNAPSHOT/reference/html/core.html#beans-p-namespace

虽然控制反转用起来还挺好使，但这对象和参数要是多起来还是得折磨🙄。