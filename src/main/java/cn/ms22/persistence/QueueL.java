package cn.ms22.persistence;

import cn.ms22.entity.CodeOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结果容器
 */
public final class QueueL {

    /**
     * 国内
     */
    public final static String INNER_QUEUE = "INNER_QUEUE";
    /**
     * 外部
     */
    public final static String OUTER_QUEUE = "OUTER_QUEUE";
    /**
     * 混合
     */
    public static final String MIXTURE = "MIXTURE";

    private static List<CodeOrder> list;
    private static Map<String, List<CodeOrder>> map;

    /**
     * 得到公用容器
     *
     * @return
     */
    public static synchronized List<CodeOrder> getInstance() {
        if (list == null) {
            list = new ArrayList<>(1 << 10);
        }
        return list;
    }

    /**
     * 多例
     * @param key
     * @return
     */
    public static synchronized List<CodeOrder> getInstance(String key) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.computeIfAbsent(key, k -> new ArrayList<>(1 << 10));
        return map.get(key);
    }
}
