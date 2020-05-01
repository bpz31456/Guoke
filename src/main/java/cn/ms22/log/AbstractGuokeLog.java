package cn.ms22.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author baopz
 * @date 2019.02.20
 */
public abstract class AbstractGuokeLog implements GuokeLog {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void info() throws GuokeLogException {
        throw new GuokeLogException("无效的日志操作。");
    }

    @Override
    public void info(GuokeLog guokeLog) throws GuokeLogException {
        throw new GuokeLogException("无效的日志操作。");
    }

    @Override
    public String extra() throws GuokeLogException {
        return null;
    }
}
