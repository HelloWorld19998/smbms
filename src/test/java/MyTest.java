import com.carry.service.user.UserService;
import com.carry.service.user.UserServiceImpl;
import org.junit.Test;

public class MyTest {
    @Test
    public void test(){
        UserServiceImpl userService = new UserServiceImpl() ;
        System.out.println("出生日期"+userService.getUserById("1").getBirthday());
    }
}
