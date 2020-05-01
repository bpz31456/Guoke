package cn.ms22.log;

/**
 * 日志记录异常
 * @author baopz
 * @date 2019.02.20
 */
public class GuokeLogException extends Exception {
    public GuokeLogException() {
        super();
    }

    public GuokeLogException(String message) {
        super(message);
    }

    public GuokeLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public GuokeLogException(Throwable cause) {
        super(cause);
    }

    protected GuokeLogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
