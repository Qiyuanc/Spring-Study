import com.qiyuan.dao.UserDaoMysqlImpl;
import com.qiyuan.service.*;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName MyTest
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/20 19:14
 * @Version 1.0
 **/
public class MyTest {
    @Test
    public void Test(){
        // 获取容器
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // 获取对象
        UserService userService = (UserService) context.getBean("userService");
        // 查询到了东西
        int i = userService.getUser();
        System.out.println(i);
    }
}
