import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @ClassName User
 * @Description TODO
 * @Author Qiyuan
 * @Date 2021/8/22 23:27
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private String name;
    private int age;
}
