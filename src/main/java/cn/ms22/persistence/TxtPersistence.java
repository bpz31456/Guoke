package cn.ms22.persistence;

import cn.ms22.entity.CodeOrder;
import cn.ms22.entity.Formator;
import cn.ms22.utils.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 保存到Txt文件中
 *
 * @author baopz
 */
public class TxtPersistence implements DataPersistence {
    private Path path;
    private Logger logger = LoggerFactory.getLogger(TxtPersistence.class);

    public TxtPersistence(String savePath) {
        path = Paths.get(savePath);
    }

    public TxtPersistence(Path path) {
        this.path = path;
    }

    @Override
    public void save(List<CodeOrder> orders) throws IOException {
        logger.info("输出路径{}。", path.toString());
        for (CodeOrder codeOrder : orders) {
            FileTools.appendInfo(path, codeOrder.format());
        }
    }

    @Override
    public void save(List<CodeOrder> orders, String... appendPath) throws IOException {
        Path tmpPath = path.getParent();
        for (String s : appendPath) {
            tmpPath = tmpPath.resolve(s);
        }
        for (CodeOrder codeOrder : orders) {
            FileTools.appendInfo(tmpPath, codeOrder.format());
        }
    }

    @Override
    public void saveAccount(String lastEmail) throws IOException {
        logger.info("持久化账号信息:{}",path.toString());
        FileTools.appendInfo(path,lastEmail);
    }
}
