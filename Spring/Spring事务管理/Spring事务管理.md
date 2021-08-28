## Spring事务管理

事务是进行数据库操作的一个关键点，将 MyBatis 和 Spring 结合起来后，事务也交由 Spring 进行管理。这里创建 Spring-11-Transaction 项目回顾之前的事务和学习 Spring 中的事务。

### 1. 回顾事务

事务的概念：在关系数据库中，一个事务可以是一条 SQL 语句，一组 SQL 语句或整个程序。

**事务的 ACID 特性**

- 原子性（ Atomicity ）：事务中包括的操作要么都做，要么都不做。
- 一致性（ Consistency ）：事务必须是使数据库从一个一致性状态变到另一个一致性状态。
- 隔离性（ Isolation ）：一个事务的执行不能被其他事务干扰。
- 持久性（ Durability ）：一个事务一旦提交，它对数据库中数据的改变就应该是永久的。

在上一节项目的基础上，回顾一下事务。把上节的项目 Copy 到本节的项目中来（略略略）。

首先给 UserMapper 接口添加新增用户和删除用户的方法

```java
public interface UserMapper {
    // 查询全部用户
    List<User> getUserList();
    // 新增用户
    int addUser(User user);
    // 删除用户
    int deleteUser(int id);
}
```

然后在 UserMapper.xml 中配置这几个方法要执行的 SQL 语句

```xml
<mapper namespace="com.qiyuan.dao.UserMapper">
    <!--select查询语句，使用别名记得配置 typeAlias -->
    <select id="getUserList" resultType="User">
        select * from user
    </select>

    <!--新增用户-->
    <insert id="addUser" parameterType="User">
        insert into user (id,name,pwd) values (#{id},#{name},#{pwd})
    </insert>

    <!--删除用户，故意写错！-->
    <delete id="deleteUser" parameterType="int">
        delete form user where id = #{id}
    </delete>

</mapper>
```

**注意**：为了后面进行事务的测试，这里故意把 from 写成了 form（也是经典错误了），让这个 SQL 语句执行起来报错。

在实现类 UserMapperImpl 中封装一下接口中的方法（这里用的是上节的 SqlSessionTemplate 方式），**同时修改一下 getUserList 方法（为了测试），让它先新增一个用户，再删除该用户，然后进行查询，这个方法就是一组事务了！**

```java
public class UserMapperImpl implements UserMapper{

    private SqlSessionTemplate sqlSession;
    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 在这里进行封装！
    public List<User> getUserList() {
        User user = new User(6, "qiyuan666", "0723");
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        // 先新增，再删掉，所以查询的结果应该没有这个用户！
        mapper.addUser(user);
        mapper.deleteUser(6);
        List<User> userList = mapper.getUserList();
        return userList;
    }

    public int addUser(User user) {
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int row = mapper.addUser(user);
        return row;
    }

    public int deleteUser(int id) {
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        int row = mapper.deleteUser(id);
        return row;
    }
    
}
```

因为增加后又删掉了，所以 getUserList 最后查询到的结果应该没有这个用户！ 

本来下一步还要注册这个实现类的，不过是 Copy 过来的配置文件，已经注册过了，直接进行测试

```java
public class MyTest {
    @Test
    public void MyBatisSpringTest(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserMapper userMapper = context.getBean("userMapper", UserMapper.class);
        List<User> userList = userMapper.getUserList();
        for (User user : userList) {
            System.out.println(user);
        }
    }
}
```

执行测试方法的结果当然会报错了，因为上面的 delete 语句就是错的！

```java
### The error occurred while setting parameters
### SQL: delete form user where id = ?
### Cause: java.sql.SQLSyntaxErrorException: You have an error in your SQL syntax; check the manual that corresponds to your MySQL server version for the right syntax to use near 'user where id = 6' at line 1
```

由于报错了所以查询的方法没执行完，所以看不到结果···我特意回去把增加和删除的两句话去掉，执行查询方法看看（其实直接数据库里看就行）！

```java
User(id=1, name=祈鸢, pwd=123456)
User(id=2, name=qiyuanc2, pwd=0723)
User(id=3, name=風栖祈鸢, pwd=07230723)
User(id=5, name=祈鸢bbb, pwd=123123)
User(id=6, name=qiyuan666, pwd=0723)
```

可以看到，**即使方法执行报错了，新增的用户还是被写到数据库中了。一组事务，三件事，新增删除查询，只成功了第一件事，这肯定是不行的，违反了事务的 ACID 原则。**

**在之前单独使用 MyBatis 的时候，可以通过在执行 mapper 的方法时捕获异常，遇到异常就调用 SqlSession 的 rollback 方法回滚，执行正常就调用 commit 方法提交。不过这里 SqlSession 被封装了起来，要怎么管理事务呢？**

**这就要用 Spring 实现了。**

### 2. Spring声明式事务

> 一个使用 MyBatis-Spring 的其中一个主要原因是它允许 MyBatis 参与到 Spring 的事务管理中。而不是给 MyBatis 创建一个新的专用事务管理器，MyBatis-Spring 借助了 Spring 中的 `DataSourceTransactionManager` 来实现事务管理。
>
> 一旦配置好了 Spring 的事务管理器，你就可以在 Spring 中按你平时的方式来配置事务。在事务处理期间，一个单独的 `SqlSession` 对象将会被创建和使用。当事务完成时，这个 session 会以合适的方式提交或回滚。
>
> 事务配置好了以后，MyBatis-Spring 将会透明地管理事务。这样在你的 DAO 类中就不需要额外的代码了。

在 Spring 中，有两种事务管理方式

- **声明式事务**：使用 AOP 实现，将事务管理切入到需要的地方
- **编程式事务**：像之前一样，在代码中的异常捕获中进行对应的提交或回滚操作

编程式事务用起来比较麻烦，需要修改 Service 层的代码，用的比较少。**而声明式事务应用了 AOP，不用改变原来的代码，用起来更灵活。**此处就只尝试使用声明式事务了。

#### 2.1 标准配置

不论使用哪种方式，都要先开启 Spring 的事务处理功能，在 Spring 的配置文件中创建一个 `DataSourceTransactionManager` 对象

这里的配置文件是 spring-dao.xml，需要的依赖是数据源 dataSource

```xml
<!--开启事务管理-->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <constructor-arg ref="dataSource" />
</bean>
```

#### 2.2 交由容器管理事务

开启事务后，就可以用 AOP 进行事务的配置了（记得引入 AOP 的约束），同时还要引入事务的约束

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans ...
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="...
        http://www.springframework.org/schema/tx
        https://www.springframework.org/schema/aop/spring-tx.xsd">
```

然后配置事务通知，`tx:advice` 使用上面的 `transactionManager` 对象进行事务管理，**这就是一个切面！**

```xml
<!--结合 AOP 实现事务的织入-->
<!--配置事务通知-->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
    <!--给哪些方法配置事务-->
    <!--配置事务的传播特性-->
    <tx:attributes>
        <tx:method name="select" propagation="REQUIRED"/>
        <!--所有方法！-->
        <tx:method name="*" propagation="REQUIRED"/>
    </tx:attributes>
</tx:advice>
```

再通过 `tx:method` 标签设置为哪些方法配置事务，同时设置**事务的传播行为**

**扩展**：事务的传播行为有七种，先了解一下！

1. REQUIRED：如果当前没有事务，就创建一个新事务，如果当前存在事务，就加入该事务，该设置是最常用的设置，也是**默认设置**。
2. SUPPORTS：支持当前事务，如果当前存在事务，就加入该事务，如果当前不存在事务，就以非事务执行。‘
3. MANDATORY：支持当前事务，如果当前存在事务，就加入该事务，如果当前不存在事务，就抛出异常。
4. REQUIRES_NEW：创建新事务，无论当前存不存在事务，都创建新事务。
5. NOT_SUPPORTED：以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。
6. NEVER：以非事务方式执行，如果当前存在事务，则抛出异常。
7. NESTED：如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行与 REQUIRED 类似的操作。

使用 `aop:pointcut` 配置切入点，再将切面切入进去！

```xml
<!--配置事务切入-->
<aop:config>
    <!--配置切入点为 com.qiyuan. 下的所有包下的所有类的所有方法，不论参数！-->
    <aop:pointcut id="txPointCut" expression="execution(* com.qiyuan.*.*.*(..))"/>
    <!--把事务切入进去-->
    <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointCut"/>
</aop:config>
```

这样事务就配置完成了！到现在，一行代码都没写，只是在进行 Spring 的配置！

再运行一下测试方法（已经把之前错误插入的 6 号用户先删了），当然还是会报错的！

```java
true
[User(id=1, name=祈鸢, pwd=123456), 
 User(id=2, name=qiyuanc2, pwd=0723), 
 User(id=3, name=風栖祈鸢, pwd=07230723), 
 User(id=5, name=祈鸢bbb, pwd=123123)]
```

不过可以看到没有 6 号用户了！说明事务回滚了！

**Debug**：遇到了一个 bug 导致事务没成功，找了半天。上面的 true 是由

```java
System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
```

输出的，可以判断本方法是否在事务管理中。一开始一直是 false，捏麻麻的找了半天发现是 execution 表达式写错了！

之前写的表达式是 ↓，代表的是 com.qiyuan 包下的所有类的所有方法！少了一级！导致切入点错了！

```java
 <aop:pointcut id="txPointCut" expression="execution(* com.qiyuan.*.*(..))"/>
```

改成 ↓ 后，就成功了！代表的是 com.qiyuan 下所有包中的所有类的所有方法！

```xml
<aop:pointcut id="txPointCut" expression="execution(* com.qiyuan.*.*.*(..))"/>
```

暂且认为这个表达式是从后往前推的！

### 3. 总结

总结一下这种方式使用事务的步骤

1. 创建 `transactionManager` 开启事务管理；
2. 创建事务通知 `tx:advice`，相当于一个切面；
3. 在 `aop:config` 中用 `aop:pointcut` 配置切入点，由 execution 表达式设置 ；
4. 用 `aop:advisor` 把切面切入到切入点中！

当然也有其他的步骤进行配置，不过核心还是**声明式事务**！

Spring 就到这里了！ヾ(￣▽￣)Bye~Bye~
