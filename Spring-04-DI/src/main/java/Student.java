import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @ClassName Student
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/22 22:01
 * @Version 1.0
 **/
@Data
@ToString
public class Student {
    // 基本类型
    private String name;
    // 其他类的对象
    private Address address;
    // 数组
    private String[] books;
    // List
    private List<String> hobbies;
    // Map
    private Map<String,String> card;
    // Set
    private Set<String> games;
    // 空值
    private String Money;
    // 配置文件属性
    private Properties info;
}
