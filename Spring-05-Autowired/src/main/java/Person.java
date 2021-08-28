import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

/**
 * @ClassName Person
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/23 16:10
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person {
    private String name;
    @Resource(name = "cat2")
    private Cat cat;
    @Resource(name = "dog2")
    private Dog dog;
}
