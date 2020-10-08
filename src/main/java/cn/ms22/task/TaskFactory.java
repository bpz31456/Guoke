package cn.ms22.task;

import cn.ms22.config.ApplicationConfig;
import cn.ms22.entity.PassWord;
import cn.ms22.persistence.DataPersistence;
import cn.ms22.persistence.TxtPersistence;
import cn.ms22.utils.DateTools;
import cn.ms22.utils.FileTools;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @author baopz
 * @date 2019.02.18
 */
public class TaskFactory {
    /**
     * 得到orderTask任务
     *
     * @param passwd
     * @param semaphore
     * @param countDownLatch
     * @return
     */
    public static Runnable getOrderTaskInstance(PassWord passwd,String place, Semaphore semaphore, CountDownLatch countDownLatch) {
        String outputPath = ApplicationConfig.getInstance().getOutput();
        Path path = Paths.get(outputPath).resolve(DateTools.currentDate()).resolve(passwd.getUsername() + FileTools.FILE_TYPE_TXT);
        DataPersistence dataPersistence = new TxtPersistence(path);
        return new OrderTask(passwd.getUsername(), passwd.getPassword(),place, semaphore, dataPersistence, countDownLatch);
    }

    public static Runnable getRegisterTask() {
        return new RegisterTask();
    }
}
