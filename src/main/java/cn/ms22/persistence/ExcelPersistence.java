package cn.ms22.persistence;

import cn.ms22.entity.CodeOrder;
import cn.ms22.utils.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Excel
 * 持久化数据
 *
 * @author baopz
 */
public class ExcelPersistence implements DataPersistence {
    private Path path;
    private Logger logger = LoggerFactory.getLogger(ExcelPersistence.class);

    public ExcelPersistence(String savePath) {
        path = Paths.get(savePath);
    }

    public ExcelPersistence(Path path) {
        this.path = path;
    }

    @Override
    public void save(List<CodeOrder> instances) throws IOException {
        logger.info("输出路径{}。", path.toString());
        if (instances == null || instances.isEmpty()) {
            return;
        }
        try {
            FileTools.appendExcelInfo(path, instances, CodeOrder.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void save(List<CodeOrder> instances, String... appendPath) throws IOException {
        Path tmpPath = path.getParent();
        for (String s : appendPath) {
            tmpPath = tmpPath.resolve(s);
        }
        if (instances == null || instances.isEmpty()) {
            return;
        }
        try {
            FileTools.appendExcelInfo(path, instances, instances.get(0).getClass());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void saveAccount(String lastEmail) {
        throw new IllegalArgumentException("待实现");
    }

}
