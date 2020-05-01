package cn.ms22.basic;

import cn.ms22.pool.DoubleCodeCache;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author bpz777@163.com
 */
public class CodeCacheTest {

    @Test
    public void test() {
        DoubleCodeCache codeCache = new DoubleCodeCache();
        codeCache.add("");
        codeCache.add(null);
        Assert.assertFalse(codeCache.contains(null));
        codeCache.add("123456");
        codeCache.add("123456");
        Assert.assertTrue(codeCache.contains("123456"));

        codeCache.add("1234");
        codeCache.add("23456");
        Assert.assertFalse(codeCache.contains("123456"));
        Assert.assertTrue(codeCache.contains("23456"));
        Assert.assertTrue(codeCache.contains("1234"));
    }
}
