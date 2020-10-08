package cn.ms22.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author baopz
 */
public final class DateTools {
    /**
     * 得到当前日期
     * @return
     */
    public synchronized static String currentDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(date);
    }

    public synchronized static String currentTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(date);
    }

    public synchronized static String currentFormatTime(String ... format) {
        Date date = new Date();
        if(format.length==0){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return simpleDateFormat.format(date);
        }else{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format[0]);
            return simpleDateFormat.format(date);
        }

    }
}
