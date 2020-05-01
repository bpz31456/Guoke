package cn.ms22.persistence;

import cn.ms22.entity.CodeOrder;
import cn.ms22.entity.Formator;

import java.io.IOException;
import java.util.List;

/**
 * @author baopz
 */
public interface DataPersistence {
    /**
     * 数据持久化
     *
     * @param instances
     * @throws IOException
     */
    void save(List<CodeOrder> instances) throws IOException;

    /**
     * 追加路径
     *
     * @param instances
     * @param appendPath
     * @throws IOException
     */
    void save(List<CodeOrder> instances, String... appendPath) throws IOException;

    /**
     * 保存账号
     * @param lastEmail 账号
     */
    void saveAccount(String lastEmail) throws IOException;
}
