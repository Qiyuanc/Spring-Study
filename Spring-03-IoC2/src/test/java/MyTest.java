import com.qiyuan.entity.User;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName MyTest
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/21 15:39
 * @Version 1.0
 **/
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
