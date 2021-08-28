## Spring整合MyBatis

了解完 Spring 的基本使用后，就可以将 Spring 和 MyBatis 结合起来使用一下了。这里创建 Spring-10-MyBatis 项目练习一下用 Spring 整合 MyBatis。

### 1. 回顾MyBatis

距离学习 MyBatis 已经有一段时间了，都快忘了怎么用了。

先尝试单独搭建一个 MyBatis 项目，有以下几步

1. 在 Maven 中导入 MyBatis 需要的依赖

   ```xml
   <dependencies>
       <!--Mysql驱动-->
       <dependency>
           <groupId>mysql</groupId>
           <artifactId>mysql-connector-java</artifactId>
           <version>8.0.22</version>
       </dependency>
       <!--MyBatis-->
       <dependency>
           <groupId>org.mybatis</groupId>
           <artifactId>mybatis</artifactId>
           <version>3.5.7</version>
       </dependency>
       <!--junit-->
       <!--Junit单元测试-->
       <dependency>
           <groupId>org.junit.jupiter</groupId>
           <artifactId>junit-jupiter-api</artifactId>
           <version>5.7.2</version>
           <scope>test</scope>
       </dependency>
   </dependencies>
   ```

   这些是 MyBatis 需要的依赖，还没有涉及到 Spring。

2. 创建配置文件 mybatis-config.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE configuration
           PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-config.dtd">
   <configuration>
       <environments default="development">
           <environment id="development">
               <transactionManager type="JDBC"/>
               <dataSource type="POOLED">
                   <property name="driver" value="com.mysql.jdbc.Driver"/>
                   <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=true&amp;serverTimezone=UTC"/>
                   <property name="username" value="root"/>
                   <property name="password" value="0723"/>
               </dataSource>
           </environment>
       </environments>
   </configuration>
   ```

   这里就不用 db.properties 文件获取属性了，后面这个工作要交给 Spring 了。

3. 创建 MyBatis 工具类，直接从之前的 MyBatis 笔记中偷过来😋

   ```java
   public class MyBatisUtil {
       // 提升作用域
       private static SqlSessionFactory sqlSessionFactory;
       static {
           try {
               // 使用MyBatis第一步：获取SqlSessionFactory对象
               String resource = "org/mybatis/example/mybatis-config.xml";
               // 要导org.apache.ibatis.io.Resources的包！ Maven犯病严重
               InputStream inputStream = Resources.getResourceAsStream(resource);
               sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   
       // 从SqlSessionFactory中获取SqlSession
       public static SqlSession getSqlSession(){
           // sqlSession 其实类似于 connection
           SqlSession sqlSession = sqlSessionFactory.openSession();
           return sqlSession;
       }
   }
   ```

   基本的统一配置就完成了，下面就是对应数据库编写实体类和对应的 Mapper 了。

   **写到测试方法的时候错回来了**：这是什么几把东西啊？

   ```java
   String resource = "org/mybatis/example/mybatis-config.xml";
   ```

   捏麻麻的，抄！抄出问题来了。配置文件放在 resources 文件夹中，直接 ↓ 就行了！

   ```java
   String resource = "mybatis-config.xml";
   ```

4. 编写实体类 User，属性对应数据库中的字段，以免多生事端

   <img src="F:\TyporaMD\Spring\Spring整合MyBatis\image-20210827125053070.png" alt="image-20210827125053070" style="zoom:67%;" />

   ```java
   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @ToString
   public class User {
       private int id;
       private String name;
       private String pwd;
   }
   ```

   顺便把 Lombok 也用上了，这玩意是真滴好用😋

5. 写完实体类，就要写对应的 Dao 层接口，即 UserMapper

   ```java
   package com.qiyuan.dao;
   ...
   public interface UserMapper {
       // 查询全部用户
       List<User> getUserList();
   }
   ```

   注意放在 dao 包下哦，差点就和 User 类放一起了。

6. 有了接口，就要有其对应的实现，即 UserMapper.xml，**这里和 User 类放在同一包下（要记得让 Maven 能导出嗷）**

   ```xml
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE mapper
           PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <!--命名空间namespace要绑定一个对应的Dao/Mapper接口，相当于实现它-->
   <mapper namespace="com.qiyuan.dao.UserMapper">
       <!--select查询语句，使用别名记得配置 typeAlias -->
       <select id="getUserList" resultType="User">
           select * from user
       </select>
   </mapper>
   ```

   这里用到了别名，要在 mybatis-config.xml 中配置哦

   ```xml
       <typeAliases>
           <package name="com.qiyuan.entity"/>
       </typeAliases>
   ```

   直接包扫描，反正现在也不用 log4j 日志😕

7. 然后不要忘记，在 mybatis-config.xml 中注册映射 mapper，特意写成一个步骤！

   ```xml
       <mappers>
           <!--要求接口和其对应的 XML 名字相同，且在同一个包下-->
           <mapper class="com.qiyuan.dao.UserMapper"/>
       </mappers>
   ```

   直接用 class 方式注册绑定，比较简洁，不过要求接口和其对应的 XML 名字相同，且在同一个包下。

8. 又来了！**配置 Maven 以让 java 文件夹中的 xml 文件能成功导出！**在 pom.xml 中添加

   ```xml
   <build>
       <resources>
           <!--让java目录下的properties和xml文件也能被导出-->
           <resource>
               <directory>src/main/java</directory>
               <includes>
                   <include>**/*.properties</include>
                   <include>**/*.xml</include>
               </includes>
               <filtering>false</filtering>
           </resource>
       </resources>
   </build>
   ```

   resources 目录下的本来就能导出，加了反而报错。

9. 到现在才是真搞完了，执行测试方法

   ```java
   public class MyTest {
       @Test
       public void getUserListTest(){
           // 不写注释了，看不懂入土吧！
           SqlSession sqlSession = MyBatisUtil.getSqlSession();
           UserMapper mapper = sqlSession.getMapper(UserMapper.class);
           List<User> userList = mapper.getUserList();
           for (User user : userList) {
               System.out.println(user);
           }
           sqlSession.close();
       }
   }
   ```

   这样就成功了，写的过程中遇到了两个 Bug：
   
   一是 MyBatis 工具类中的资源文件路径写错了，见3；
   
   二是 Maven 的配置导出问题了，见8。

最基础的 MyBatis 应用就完成了，接下来引入 Spring。

### 2. MyBatis-Spring

通过 Spring 使用 MyBatis 有两种方式：使用 SqlSessionTemplate 和使用 SqlSessionDaoSupport。

#### 2.1 导入依赖

要将 MyBatis 和 Spirng 结合起来，除了导入上面 MyBatis 的依赖，当然还要有 Spring 的依赖

```xml
<dependencies>
    <!--上面 MyBtais 的包-->
    ...
    <!-- Spring 框架 -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>5.3.9</version>
    </dependency>
    <!-- AOP -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.9.7</version>
    </dependency>
    <!-- Spring 管理 JDBC -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>5.3.9</version>
    </dependency>
    <!-- mybatis-spring -->
    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis-spring</artifactId>
        <version>2.0.6</version>
    </dependency>
</dependencies>
```

**注意这里相比之前的 Spring 项目，多了 spring-jdbc 和 mybatis-spring 的包，前者用于 Spring 管理数据库，后者作用就是是将 MyBatis 和 Spring 结合起来。**

> **什么是 MyBatis-Spring？**
>
> MyBatis-Spring 会帮助你将 MyBatis 代码无缝地整合到 Spring 中。它将允许 MyBatis 参与到 Spring 的事务管理之中，创建映射器 mapper 和 `SqlSession` 并注入到 bean 中，以及将 Mybatis 的异常转换为 Spring 的 `DataAccessException`。 最终，可以做到应用代码不依赖于 MyBatis，Spring 或 MyBatis-Spring。

#### 2.2 使用SqlSessionTemplate

> `SqlSessionTemplate` 是 MyBatis-Spring 的核心。作为 `SqlSession` 的一个实现，这意味着可以使用它无缝代替你代码中已经在使用的 `SqlSession`。 `SqlSessionTemplate` 是线程安全的，可以被多个 DAO 或映射器所共享使用。

通过 Spring 去使用 MyBatis 的步骤为

1. **创建 spring-dao.xml 配置文件**（也可以是其他名字啦），管理数据库的配置，也相当于 MyBatisUtil 工具类

   配置数据源 dataSource

   ```xml
   <!-- 用 Spring 的数据源 替换 MyBatis 的数据源 -->
   <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
       <!--就是 mybatis-config 中 数据源 dataSource 的属性！-->
       <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
       <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=true&amp;serverTimezone=UTC"/>
       <property name="username" value="root"/>
       <property name="password" value="0723"/>
   </bean>
   ```

   在 Spring 中配置了数据源，mybatis-config 中的就可以删掉了

   ```xml
   <!-- mybatis-config.xml -->
   <environments default="...">
   	<!--用不到了，删了吧！-->
   </environments>
   ```

2. **创建 sqlSessionFactory 的 bean**，同时设置数据源 dataSource 属性为上面配置的数据源，设置 configLocation 属性以绑定 MyBatis 配置文件

   ```xml
   <!-- sqlSessionFactory -->
   <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
       <property name="dataSource" ref="dataSource" />
       <!--绑定 MyBatis 配置文件！-->
       <property name="configLocation" value="classpath:mybatis-config.xml"/>
   </bean>
   ```

   这里相当于 MyBatisUtil 工具类中的获取 SqlSessionFactory 实例！

   ```java
   public class MyBatisUtil {
       private static SqlSessionFactory sqlSessionFactory;
       static {
           try {
               // 使用MyBatis第一步：获取SqlSessionFactory对象
               String resource = "mybatis-config.xml";
               InputStream inputStream = Resources.getResourceAsStream(resource);
               sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       ...
   }
   ```

3. **在 sqlSessionFactory 的 bean 中也可以配置其属性**，和在 mybatis-config 中配置是一样的！如注册 Mapper

   ```xml
   <!-- sqlSessionFactory -->
   <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
   	...
       <!--如，在这里注册 Mapper -->
       <property name="mapperLocations" value="classpath:com/qiyuan/dao/*.xml"/>
   </bean>
   ```

   这里就用到之前 MyBatis 中不能用的通配符了，因为通配符是由 Spring 提供的！

   在 bean 中配置了，mybatis-config 中配置的 mapper 也可以删掉了

   ```xml
   <!-- mybatis-config.xml -->
   <mappers>
       <!--不用了！-->
   </mappers>
   ```

   这样一来，mybatis-config.xml 中几乎已经没有内容了（还剩一个别名 typeAlias ），虽然别名也能在 bean 中配置，不过最好将别名 typeAlias 和设置 settings 放在 mybatis-config.xml 中，方便查看和修改（给 MyBatis 留点面子）。

   ```xml
   <!--仅存的 mybatis-conifg 内容-->
   <?xml version="1.0" encoding="UTF-8" ?>
   <!DOCTYPE configuration
           PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
           "http://mybatis.org/dtd/mybatis-3-config.dtd">
   <configuration>
       <typeAliases>
           <package name="com.qiyuan.entity"/>
       </typeAliases>
   
   </configuration>
   ```

4. **有了配置好的 sqlSessionFactory 后，就可以用它获取 sqlSession了。**

   创建 sqlSession 的 bean，注入  sqlSessionFactory 依赖

   ```xml
   <!-- SqlSessionTemplate 就是 SqlSession！-->
   <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
       <!--只能用构造器注入，因为它没有 set 方法！-->
       <!-- 把工厂给它，就能从中 get 到 SqlSession 了-->
       <constructor-arg index="0" ref="sqlSessionFactory"/>
   </bean>
   ```

   SqlSessionTemplate 类只能通过构造器注入 sqlSessionFactory 依赖，**这就是一个 SqlSession 了**。

5. 重点来了！**由于面向对象的思想，要把对象交给 Spring 管理，而之前使用 MyBatis 时，Mapper.xml 充当了接口的实现类，这个实现类无法让 Spring 管理，所以要写一个真正的接口实现类，封装 Mapper.xml，交给 Spring 管理！**

   创建 UserMapperImpl 类，实现了 UserMapper 接口，即有对数据库操作的方法

   ```java
   public class UserMapperImpl implements UserMapper{
       // 原来的操作，使用 SqlSession 实现，现在使用 SqlSessionTemplate
       // 不过还是叫做 sqlSession 亲切！
       private SqlSessionTemplate sqlSession;
   
       // 添加 set 方法，以注入属性
       public void setSqlSession(SqlSessionTemplate sqlSession) {
           this.sqlSession = sqlSession;
       }
   
       // 在这里进行封装！
       public List<User> getUserList() {
           // IoC 的思想！不用去 new 一个 sqlSession 了，注入后就能用！
           UserMapper mapper = sqlSession.getMapper(UserMapper.class);
           List<User> userList = mapper.getUserList();
           return userList;
       }
   }
   ```

   其中，有一个属性 sqlSession（变成了 SqlSessionTemplate 也是一样用法）及其对应的 set 方法，通过 Spring 依赖注入后就能使用，相当于之前的

   ```java
   SqlSession sqlSession = MyBatisUtil.getSqlSession();
   // 关闭应该是由 Spring 管理的吧...
   ```

   在这个“真”实现类（ UserMapperImpl 类）中调用了“假”实现类（ UserMapper.xml ）的方法，相当于多了一层封装，也变成了一个真实存在的类，方便 Spring 管理！

6. **把真实现类交给 Spring 管理，同时进行依赖注入**

   ```xml
   <bean id="userMapper" class="com.qiyuan.dao.UserMapperImpl">
       <!--注入 sqlSession！-->
       <property name="sqlSession" ref="sqlSession"/>
   </bean>
   ```

   这时，获取 userMapper 对象后执行其中的方法，就会到 UserMapper.xml 执行对应的语句，和之前区别不大，只是多了一层封装以方便管理！

7. 现在就可以用起来试一试了，测试方法

   ```java
   public class MyTest {
       @Test
       public void MyBatisSpringTest(){
           ApplicationContext context = new ClassPathXmlApplicationContext("spring-dao.xml");
           UserMapper userMapper = context.getBean("userMapper", UserMapper.class);
           List<User> userList = userMapper.getUserList();
           for (User user : userList) {
               System.out.println(user);
           }
       }
   }
   // 执行结果
   /*
       User(id=1, name=祈鸢, pwd=123456)
       User(id=2, name=qiyuanc2, pwd=0723)
       User(id=3, name=風栖祈鸢, pwd=07230723)
       User(id=5, name=祈鸢bbb, pwd=123123)
   */
   ```

   执行成功！相比之前，更加简洁明了了。获取 context 容器，获取容器的的对象，调用对象的方法，一气呵成！

**优化**：可以注意到，大部分配置都在 spring-dao.xml 文件中，这个文件做了几件事

1. 配置数据源，即连接数据库的一些配置（ mybatis-config.xml 中的 environment 部分）
2. 创建 sqlSessionFactory 的 bean，进行依赖注入（ mybatis-config.xml 中的 mapper 部分，MyBatisUtil 的创建 sqlSessionFactory 部分）
3. 创建 sqlSession 的 bean，将 sqlSessionFactory 注入进去（ MyBatisUtil 中的 sqlSessionFactory.openSession ）
4. 创建真实现类 UserMapperImpl 的 bean，为其注入 sqlSession

其中，第1、2、3步都是配置和工具类干的事情，属于改动比较少的部分；而第4步属于会经常会进行的步骤，如增加 StudentMapperImpl、TeacherMapperImpl 等的 bean。

所以将第4步这种配置抽出来，留下1、2、3步，**使得 spring-dao.xml 变成了一个比较纯洁的配置文件**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- 用 Spring 的数据源 替换 MyBatis 的数据源 -->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <!--就是 mybatis-config 中 数据源 dataSource 的属性！-->
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useUnicode=true&amp;characterEncoding=utf-8&amp;useSSL=true&amp;serverTimezone=UTC"/>
        <property name="username" value="root"/>
        <property name="password" value="0723"/>
    </bean>

    <!-- sqlSessionFactory -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource" />
        <!--绑定 MyBatis 配置文件！-->
        <property name="configLocation" value="classpath:mybatis-config.xml"/>
        <!--如，在这里注册 Mapper -->
        <property name="mapperLocations" value="classpath:com/qiyuan/dao/*.xml"/>
    </bean>
    
    <!-- SqlSessionTemplate 就是 SqlSession！-->
    <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
        <!--只能用构造器注入，因为它没有 set 方法！-->
        <!-- 把工厂给它，就能从中 get 到 SqlSession 了-->
        <constructor-arg index="0" ref="sqlSessionFactory"/>
    </bean>
</beans>
```

至于第4步这种操作，创建了一个要具体用到的对象，还是放到 applicationContext.xml 中进行管理吧！

创建 applicationContext.xml，**通过 import 标签引入 spring-dao.xml，把真正要用到的对象，即 userMapper 交给它管理**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd">

    <!--通过 import 标签引入 spring-dao.xml -->
    <import resource="spring-dao.xml"/>

    <bean id="userMapper" class="com.qiyuan.dao.UserMapperImpl">
        <!--注入 sqlSession！-->
        <property name="sqlSession" ref="sqlSession"/>
    </bean>
    
</beans>
```

这样几个配置文件的作用都很明确了，mybatis-config 负责 MyBatis 的一些配置（别名、设置），spring-dao 管理了 MyBatis 连接数据库、创建 SqlSession和注册 mapper 的配置，applicationContext 整合了 Spring 的配置（现在是 spring-dao，后面还会有 spring-mvc 等等）和管理要用到对象。

**记得加载配置文件的时候加载 applicationContext.xml 哦！**

#### 2.3 使用SqlSessionDaoSupport

使用 SqlSessionDaoSupport 与使用 SqlSessionTemplate 大同小异，只不过更简化了一点。

上面说到，UserMapperImpl 类中有一个 SqlSessionTemplate 类型的 sqlSession 属性，实现的方法中封装了使用 SqlSession 对数据库的操作，调用它的方法就相当于在使用 SqlSession。

**这种方式在使用前需要注入 sqlSession 属性，而 SqlSession 又由 SqlSessionFactory 创建。也就是说，使用这种方式需要 SqlSessionFactory 和 SqlSession 的 bean。**

**先说结论，使用 SqlSessionDaoSupport 省略了创建 SqlSession 的 bean 的步骤。**创建 UserMapperImpl2 实现类，继承 SqlSessionDaoSupport 类，实现 UserMapper 接口

```java
public class UserMapperImpl2 extends SqlSessionDaoSupport implements UserMapper{
    public List<User> getUserList() {
        return null;
    }
}
```

重点来了！**SqlSessionDaoSupport 类中有 getSqlSession 方法，可以直接获得一个 sqlSession！**用这个 sqlSession 去执行数据库操作就行了！

```java
public class UserMapperImpl2 extends SqlSessionDaoSupport implements UserMapper{
    public List<User> getUserList() {
        SqlSession sqlSession = getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> userList = mapper.getUserList();
        return userList;
    }
}
```

能用是能用，但必须思考为什么能用。**为什么 SqlSessionDaoSupport 类可以通过 getSqlSession 方法返回一个 SqlSession？**我们知道，SqlSession 是由 SqlSessionFactory 创建的，所以，点进去看看

```java
public abstract class SqlSessionDaoSupport extends DaoSupport {
	private SqlSessionTemplate sqlSessionTemplate;
    
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        // 从工厂获取 sqlSessionTemplate
    }
}
```

原来如此！**SqlSessionDaoSupport 类中就需要注入一个 sqlSessionFactory，以获取其中的 sqlSessionTemplate 对象，返回的就是这个对象！**

所以在注册 UserMapperImpl2 的实现类的时候，要注入 sqlSessionFactory 依赖

```xml
<bean id="userMapper2" class="com.qiyuan.dao.UserMapperImpl2">
    <!--注入 sqlSessionFactory！-->
    <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
</bean>
```

它会通过注入的 sqlSessionFactory，获取 sqlSessionTemplate，也就是之前的方式获取到的 SqlSession 了（回见 2.2 / 4. ）。

执行测试方法，获取的是 userMapper2 对象，执行结果相同！

```java
public class MyTest {
	@Test
    public void MyBatisSpringTest2(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        UserMapper userMapper = context.getBean("userMapper2", UserMapper.class);
        List<User> userList = userMapper.getUserList();
        for (User user : userList) {
            System.out.println(user);
        }
    }
}
```

这样就完成了，**相比直接使用 SqlSessionTemplate 的方式，SqlSessionDaoSupport 将其封装了起来，从 SqlSessionDaoSupport 中就可以获取到 SqlSession，不用配置 sqlSession 的 bean 和注入了。**

### 3. 总结

**使用 MyBatis-Spring 有两种方式**

- 使用SqlSessionTemplate：就是直接使用 SqlSession，需要将 SqlSession 注入到实现类中进行使用。
- 使用SqlSessionDaoSupport：实现类继承 SqlSessionDaoSupport 类，把工厂交给它（通过注入），就能从它获取 SqlSession。

使用 MyBatis-Spring 需要创建与 Mapper.xml 对应的实现类，在实现类中调用 Mapper.xml 实现数据库操作。**实例化 Mapper.xml 为一个实现类的目的是使 Spring 能管理它。**

**使用 SqlSessionTemplate 的步骤**

1. 配置数据源 dataSource，可以是 dbcp、c3p0、spring-jdbc 等
2. 用数据源 dataSource 创建 SqlSessionFactory，并绑定 MyBatis 配置文件
3. 设置 SqlSessionFactory 的属性（可选），也就是 MyBatis 中的配置
4. 创建 SqlSession（ SqlSessionTemplate 类型），通过构造器注入 SqlSessionFactory 依赖
5. 实现类中加入 SqlSession 属性，方法直接用它（配置 bean 的时候注入 SqlSession 依赖）

**如果使用的是 SqlSessionDaoSupport** 

4. 实现类继承 SqlSessionDaoSupport 类，给实现类注入 SqlSessionFactory 依赖
5. 实现类中，方法使用 getSqlSession 获取 SqlSession

内容好多，越写越乱！希望后面看得懂😵...
