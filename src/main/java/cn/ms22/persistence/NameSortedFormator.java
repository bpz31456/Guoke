package cn.ms22.persistence;

import cn.ms22.entity.CodeOrder;
import cn.ms22.entity.Formator;

import java.util.Comparator;
import java.util.List;

/**
 * @author baopz
 * @date 2019.02.18
 */
public class NameSortedFormator implements Formator {
    private List<CodeOrder> orders;

    public NameSortedFormator(List<CodeOrder> orders) {
        this.orders = orders;
    }

    @Override
    public String format() {
        orders.sort(Comparator.comparing(CodeOrder::getName));
        return null;
    }
}
