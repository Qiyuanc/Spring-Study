import com.qiyuan.config.MyConfig;
import com.qiyuan.entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @ClassName MyTest
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/24 17:34
 * @Version 1.0
 **/
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        User getUser = context.getBean("getUser", User.class);
        System.out.println(getUser);
    }

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
