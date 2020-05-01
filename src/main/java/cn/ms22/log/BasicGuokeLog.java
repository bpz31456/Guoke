package cn.ms22.log;

import cn.ms22.entity.CodeOrder;
import cn.ms22.utils.FileTools;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;

/**
 * @author baopz
 */
public class BasicGuokeLog<T> extends AbstractGuokeLog {
    private T t;
    private Path path;

    public BasicGuokeLog(T t, Path path) {
        this.t = t;
        this.path = path;
    }

    @Override
    public void info() throws GuokeLogException {
        try {
            logger.debug("{}", t.getClass());
            logger.debug("参数化{}", (t.getClass().getGenericSuperclass() instanceof ParameterizedType));
            logger.debug("参数化{}", (ParameterizedType.class.isInstance(t.getClass().getGenericSuperclass())));

            logger.debug("===={}", t.getClass().getComponentType());
            logger.debug("{}", t.getClass().getTypeParameters().getClass().getComponentType());
            logger.debug("{}", t.getClass().getTypeParameters().getClass());
            logger.debug("{}", t.getClass().getTypeParameters().getClass().getName());
            if (List.class.isAssignableFrom(t.getClass())) {
                List list = (List) t;
                for (Object o : list) {
                    FileTools.appendInfo(path, o.toString());
                }
            }
        } catch (IOException e) {
            logger.warn("日志记录失败");
        }
    }

    @Override
    public String extra() throws GuokeLogException {
        String info = "";

        if (ParameterizedType.class.isInstance(t.getClass().getGenericSuperclass())) {
            ParameterizedType type = (ParameterizedType) t.getClass().getGenericSuperclass();
            Type[] actualTypeArguments = type.getActualTypeArguments();
            if (CodeOrder.class.isInstance(actualTypeArguments[0].getClass())) {
                info += "CodeOrder 统计信息\n";
            }
        }

        if (List.class.isAssignableFrom(t.getClass())) {
            List list = (List) t;
            info += "总计：" + list.size() + "条";
        }
        return info;
    }
}
