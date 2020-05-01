package cn.ms22.log;

/**
 * 系统日志记录接口
 * @author baopz
 */
public interface GuokeLog {
    /**
     * 基础日志记录
     */
    void info() throws GuokeLogException;

    /**
     * 包装类日志记录
     * @param guokeLog
     */
    void info(GuokeLog guokeLog) throws GuokeLogException;

    /**
     * 对外提供额外的信息
     * @return
     * @throws GuokeLogException
     */
    String extra() throws GuokeLogException;

}
