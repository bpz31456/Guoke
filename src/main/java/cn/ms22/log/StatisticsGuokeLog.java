package cn.ms22.log;

import cn.ms22.utils.FileTools;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 统计信息
 *
 * @author baopz
 */
public class StatisticsGuokeLog extends AbstractGuokeLog {
    private Path path;

    public StatisticsGuokeLog(Path path) {
        this.path = path;
    }

    @Override
    public void info(GuokeLog guokeLog) throws GuokeLogException {
        //统计
        if (guokeLog.extra() != null) {
            try {
                FileTools.appendInfo(path, guokeLog.extra());
            } catch (IOException e) {
                logger.warn("统计信息有误。");
            }
        }
    }
}
