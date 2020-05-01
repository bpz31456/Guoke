package cn.ms22.persistence;

import cn.ms22.entity.PassWord;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 失败容器
 * @author baopz
 */
public final class QueueR {
    private static LinkedBlockingQueue<PassWord> queue;

    /**
     * 得到公用容器
     * @return
     */
    public static synchronized LinkedBlockingQueue<PassWord> getInstance() {
        if (queue == null) {
            queue = new LinkedBlockingQueue<PassWord>(1 << 10);
        }
        return queue;
    }
}
