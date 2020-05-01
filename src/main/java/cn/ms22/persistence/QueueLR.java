package cn.ms22.persistence;

import cn.ms22.entity.PassWord;

import java.util.ArrayList;
import java.util.List;

/**
 * 失败结果
 */
public final class QueueLR {
    private static List<PassWord> list;

    /**
     * 得到公用容器
     * @return
     */
    public static synchronized List<PassWord> getInstance() {
        if (list == null) {
            list = new ArrayList<>(1 << 10);
        }
        return list;
    }
}
