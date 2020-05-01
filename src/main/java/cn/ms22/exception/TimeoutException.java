package cn.ms22.exception;

/**
 * @author bpz777@163.com
 */
public class TimeoutException extends Exception {
    public TimeoutException() {
        super();
    }

    public TimeoutException(String message) {
        super(message);
    }
}
