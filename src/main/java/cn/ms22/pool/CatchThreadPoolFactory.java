package cn.ms22.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Baopz
 * @date 2018/04/23
 * @since 1.0.0
 */
public class CatchThreadPoolFactory {

    protected static Map<String, ThreadPoolExecutor> poolExecutors = new HashMap<>(2 << 4);

    /**
     * @param key
     * @param threadName
     * @return
     * @since 2.0.0
     * 得到线程池
     */
    public static synchronized ThreadPoolExecutor getInstance(String key, String threadName) {
        //如果已经shutdown了就需要删除掉
        ThreadPoolExecutor threadPoolExecutor = poolExecutors.get(key);
        if (threadPoolExecutor != null && threadPoolExecutor.isShutdown()) {
            poolExecutors.remove(key);
        }
        return poolExecutors.computeIfAbsent(key, s -> iniPoolExecutor(threadName));
    }

    public static synchronized ThreadPoolExecutor getSingleInstance(String key, String threadName) {
        return poolExecutors.computeIfAbsent(key, s -> iniSingleThreadPool(threadName));
    }

    private static ThreadPoolExecutor iniSingleThreadPool(String threadName) {
        ThreadFactoryBuilder factoryBuilder = new ThreadFactoryBuilder().setNameFormat(threadName + "-%d");
        ThreadFactory threadFactory = factoryBuilder.build();
        return new ThreadPoolExecutor(1,
                1, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1), threadFactory);
    }

    /**
     * 这里的LinkedBlockingQueue与ArrayBlockingQueue区别
     **/
    private static ThreadPoolExecutor iniPoolExecutor(String threadName) {
        ThreadFactory threadNameFactory = new ThreadFactoryBuilder().setNameFormat(threadName + "%d").build();
        return new ThreadPoolExecutor(20,
                1 << 13,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                threadNameFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
