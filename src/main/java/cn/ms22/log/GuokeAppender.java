package cn.ms22.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import cn.ms22.interfaces.LogBuffer;

/**
 * 格式化日志输出
 */
public class GuokeAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }

    private Layout<ILoggingEvent> layout;

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (eventObject == null || !isStarted()) {
            return;
        }
        String msg = layout.doLayout(eventObject);
        LogBuffer.put(msg);
    }

    @Override
    public void start() {
        //这里可以做些初始化判断 比如layout不能为null ,
        if (layout == null) {
            addWarn("Layout was not defined");
        }
        //或者写入数据库 或者redis时 初始化连接等等
        super.start();
    }

    @Override
    public void stop() {
        //释放相关资源，如数据库连接，redis线程池等等
        System.out.println("logback-stop方法被调用");
        if (!isStarted()) {
            return;
        }
        super.stop();
    }
}
