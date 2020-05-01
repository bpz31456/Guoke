package cn.ms22.log;

import cn.ms22.entity.CodeOrder;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LogTest {
    @Test
    public void test(){
        List<CodeOrder> list = new ArrayList<>();
        CodeOrder codeOrder = new CodeOrder();
        codeOrder.setCode("code");
        codeOrder.setName("name");
        codeOrder.setPlatform("platform");
        codeOrder.setUsername("username");
        list.add(codeOrder);
        BasicGuokeLog<List<CodeOrder>> log = new BasicGuokeLog<>(list, Paths.get("C:\\Users\\baopz\\Desktop\\Guoke\\test\\1.txt"));
        try {
            log.info();
        } catch (GuokeLogException e) {
            e.printStackTrace();
        }
    }
}
