package cn.ms22.driver;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathTest {

    @Test
    public void test() {
        Path path = Paths.get("C:\\Users\\baopz\\Desktop\\Guoke\\20190219\\1.txt");
        Path subPath = path.getName(path.getNameCount() - 1);
        subPath = path.getParent().resolve("test").resolve(subPath);
        System.out.println(subPath);
    }

}
