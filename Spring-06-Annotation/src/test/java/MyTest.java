import com.qiyuan.entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName MyTest
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/24 13:06
 * @Version 1.0
 **/
public class MyTest {
    @Test
    public void Test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        User user = context.getBean("user1", User.class);
        System.out.println(user.name);
    }
}
