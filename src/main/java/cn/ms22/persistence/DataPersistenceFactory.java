package cn.ms22.persistence;

import java.nio.file.Path;

/**
 * @author bpz777@163.com
 */
public final class DataPersistenceFactory {
    private static DataPersistence dataPersistence;

    public synchronized static DataPersistence getExcelDataPersistenceInstance(Path path) {
        if (dataPersistence == null) {
            dataPersistence = new ExcelPersistence(path);
        }
        return dataPersistence;
    }

    public synchronized static DataPersistence getExcelDataPersistenceInstance(String path) {
        if (dataPersistence == null) {
            dataPersistence = new ExcelPersistence(path);
        }
        return dataPersistence;
    }

    public synchronized static DataPersistence getTxtDataPersistenceInstance(String path) {
        if (dataPersistence == null) {
            dataPersistence = new TxtPersistence(path);
        }
        return dataPersistence;
    }

    public synchronized static DataPersistence getTxtDataPersistenceInstance(Path path) {
        if (dataPersistence == null) {
            dataPersistence = new TxtPersistence(path);
        }
        return dataPersistence;
    }
}
