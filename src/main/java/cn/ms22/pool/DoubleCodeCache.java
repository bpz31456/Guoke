package cn.ms22.pool;

/**
 * @author bpz777@163.com
 */
public final class DoubleCodeCache {
    private final int limit = 2;
    private int curIndex = -1;
    private String[] cache = new String[limit];

    public void add(String code) {
        if (isNull(code)) {
            return;
        }
        code = code.trim();
        if (contains(code)) {
            return;
        }
        if (curIndex < limit - 1) {
            cache[++curIndex] = code;

        } else if (curIndex == limit - 1) {
            cache[0] = code;
            curIndex = 0;
        }
    }

    public boolean contains(String code) {
        if (isNull(code)) {
            return false;
        }
        for (String s : cache) {
            if (code.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNull(String code) {
        return code == null || "".equals(code.trim());
    }
}
