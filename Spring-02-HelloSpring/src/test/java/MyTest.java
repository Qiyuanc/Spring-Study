import com.qiyuan.entity.Hello;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName MyTest
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/21 13:43
 * @Version 1.0
 **/
public class MyTest {
    @Test
    public void HelloSpring(){
        // 获取 Spring 容器，使用 xml 配置必经步骤！
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // 对象都被 Spring 管理了，要使用取出来即可，参数就是配置的 id
        Hello hello = (Hello) context.getBean("hello");
        // 使用一下对象！
        System.out.println(hello);
    }
}
