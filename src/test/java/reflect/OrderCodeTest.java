package reflect;


import cn.ms22.entity.CodeOrder;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class OrderCodeTest {
    @Test
    public void test(){
        Field[] declaredFields = CodeOrder.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(declaredField.getType().isAssignableFrom(List.class));
            System.out.println(declaredField.getType());
        }
    }
}
