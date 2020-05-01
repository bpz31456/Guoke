package cn.ms22.entity;

/**
 * 订单页面的Order
 * @author baopz
 */
public class Order {
    private String name;
    private String date;
    private String orderId;
    private String prices;
    private String status;
    private String link;

    /**详情页面状态**/
    private String completeStatus;
    /**领取激活码连接**/
    private String codeLink;

    @Override
    public String toString() {
        return "Order{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", orderId='" + orderId + '\'' +
                ", prices='" + prices + '\'' +
                ", status='" + status + '\'' +
                ", link='" + link + '\'' +
                ", completeStatus='" + completeStatus + '\'' +
                ", codeLink='" + codeLink + '\'' +
                '}';
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
