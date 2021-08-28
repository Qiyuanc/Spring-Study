import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName MyTest
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/22 22:22
 * @Version 1.0
 **/
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Student student = (Student) context.getBean("student");
        System.out.println(student.toString());
    }

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
}
