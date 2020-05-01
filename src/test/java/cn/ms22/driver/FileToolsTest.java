package cn.ms22.driver;

import cn.ms22.utils.DateTools;
import cn.ms22.utils.FileTools;

import java.io.IOException;
import java.nio.file.Paths;

public class FileToolsTest {

    public void test(){
        try {
            FileTools.appendInfo(Paths.get(DateTools.currentDate()+".txt"),"跟我来.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
