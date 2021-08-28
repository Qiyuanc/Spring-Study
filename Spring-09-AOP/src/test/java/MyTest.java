import com.qiyuan.service.UserService;
import com.qiyuan.service.UserServiceImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName MyTest
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/26 18:44
 * @Version 1.0
 **/
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
