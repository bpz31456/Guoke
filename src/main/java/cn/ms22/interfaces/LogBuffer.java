package cn.ms22.interfaces;

import cn.ms22.utils.DateTools;

/**
 * 日志缓存
 */
public class LogBuffer {
    private static StringBuffer stringBuffer = new StringBuffer(1 << 10);

    /**
     * 设置info
     *
     * @param info
     */
    public static synchronized void put(String info) {
        if(info==null || info.trim().equals("")) return;
        stringBuffer.append("\n").append(DateTools.currentFormatTime()).append(" ").append(info);
    }

    /**
     * 得到info
     *
     * @return
     */
    public static synchronized String get() {
        String info = stringBuffer.toString();
        stringBuffer.delete(0, stringBuffer.length());
        return info;
    }

    /**
     * 是否为空
     *
     * @return
     */
    public static synchronized boolean isEmp() {
        return stringBuffer.length() == 0;
    }

}
