package reflect;

import cn.ms22.entity.CodeOrder;
import org.junit.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bpz777@163.com
 */
public class ClassTest {

    @Test
    public void test() {
        List<CodeOrder> codeOrders = new ArrayList<>();
        for (TypeVariable<? extends Class<? extends List>> classTypeVariable : codeOrders.getClass().getTypeParameters()) {
            for (Type type : classTypeVariable.getBounds()) {
                System.out.println("bounds:"+type.getTypeName());
            }
            System.out.println(classTypeVariable.getTypeName());
            System.out.println("classTypeVariable:"+classTypeVariable.getName());
        }
    }

}
