package cn.ms22.thread;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @author bpz777@163.com
 */
public class CyclicTest {
    private Logger logger = LoggerFactory.getLogger(CyclicTest.class);

    @Test
    public void test1() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
        Thread thread = new Thread(() -> {
            try {
                logger.debug("睡眠10秒");
                TimeUnit.SECONDS.sleep(10);
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        logger.debug("执行main线程");
    }
}
