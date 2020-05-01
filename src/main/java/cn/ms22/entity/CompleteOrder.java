package cn.ms22.entity;

/**
 * 完成页面的Order
 * @author baopz
 */
public class CompleteOrder {
    private String name;
    private String count;
    private String prices;
    private String type;
    private String completeStatus;
    private String codeLink;


    @Override
    public String toString() {
        return "CompleteOrder{" +
                "name='" + name + '\'' +
                ", count='" + count + '\'' +
                ", prices='" + prices + '\'' +
                ", type='" + type + '\'' +
                ", completeStatus='" + completeStatus + '\'' +
                ", codeLink='" + codeLink + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(String completeStatus) {
        this.completeStatus = completeStatus;
    }

    public String getCodeLink() {
        return codeLink;
    }

    public void setCodeLink(String codeLink) {
        this.codeLink = codeLink;
    }
}
