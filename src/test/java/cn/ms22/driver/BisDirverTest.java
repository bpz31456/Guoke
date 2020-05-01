package cn.ms22.driver;

import cn.ms22.entity.CodeOrder;
import cn.ms22.entity.CompleteOrder;
import cn.ms22.entity.Order;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BisDirverTest {

    private Logger logger = LoggerFactory.getLogger(BisDirverTest.class);

    public void test() {
        WebDriver driver = new ChromeDriver(iniChromeOptions());
        //登录
        try {
            login(driver);
            TimeUnit.SECONDS.sleep(2);
            driver.get("https://www.sonkwo.com/setting/game_factory");
            TimeUnit.SECONDS.sleep(2);

            List<WebElement> elements = driver.findElements(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div.game-factory-list-container "));
            List<CodeOrder> codeOrders = new ArrayList<>();
            for (int i = 1; i <= elements.size(); i++) {
                WebElement tmp = elements.get(i - 1);

                List<WebElement> webElements = tmp.findElements(By.cssSelector("div.game-list"));
                for (WebElement element : webElements) {
                    CodeOrder codeOrder = new CodeOrder();
                    String name = element.findElement(By.cssSelector("div.game-list-left > div")).getText();
                    String platform = element.findElement(By.cssSelector("div.game-type")).getText();
                    element.findElement(By.cssSelector("div.btn.steam-key")).click();
                    TimeUnit.MILLISECONDS.sleep(500);
                    String code = driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div:nth-child("+i+") > div.overlayfixed.undefined > div > div > div.overlaybox-body > div > div > div.key-code")).getText();

                    codeOrder.setName(name);
                    codeOrder.setPlatform(platform);
                    codeOrder.setCode(code);
                    TimeUnit.MILLISECONDS.sleep(500);
                    driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div:nth-child("+i+") > div.overlayfixed.undefined > div > div > div.overlaybox-body > div > h5 > div > i")).click();
                    TimeUnit.SECONDS.sleep(1);

                    codeOrders.add(codeOrder);
                }

            }

            codeOrders.forEach(System.out::println);
            //
            //到主界面，列表
//            List<Order> orders = toOrdersPage(driver);

            //详情列表
//            List<CompleteOrder> completeOrders = toCompleteOrders(driver, orders);

            //
//            for (CompleteOrder completeOrder : completeOrders) {
//                driver.get(completeOrder.getCodeLink());
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //详情页面提取
//        toDetailPage(driver);
//        driver.close();
    }

    private List<CompleteOrder> toCompleteOrders(WebDriver driver, List<Order> orders) throws InterruptedException {
        List<CompleteOrder> completeOrders = new ArrayList<>();
        for (Order order : orders) {
            driver.get(order.getLink());
            TimeUnit.SECONDS.sleep(1);


            //详情
            List<WebElement> elements = driver.findElements(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.order-list > div.order-wrap > div.list-wrap > div"));
            for (WebElement element : elements) {

                CompleteOrder completeOrder = new CompleteOrder();

                String name = element.findElement(By.cssSelector("div.row1 > div > div")).getText();
                String count = element.findElement(By.cssSelector("div.row2")).getText();
                String prices = element.findElement(By.cssSelector("div.row3")).getText();
                String type = element.findElement(By.cssSelector("div.row4")).getText();
                String completeStatus = element.findElement(By.cssSelector("div.row5.row5-factory > div > a.completed-link")).getText();
                String codeLink = element.findElement(By.cssSelector("div.row5.row5-factory > div > a.active_btn")).getAttribute("href");

                completeOrder.setName(name);
                completeOrder.setCount(count);
                completeOrder.setPrices(prices);
                completeOrder.setType(type);
                completeOrder.setCompleteStatus(completeStatus);
                completeOrder.setCodeLink(codeLink);

                completeOrders.add(completeOrder);
            }
            System.out.println(completeOrders.size());
            completeOrders.forEach(System.out::println);
            //详情页面处理
        }
        return completeOrders;
    }

    private List<Order> toOrdersPage(WebDriver driver) throws InterruptedException {
        //查询列表
        driver.get("https://www.sonkwo.com/setting/orders?type=all");
        TimeUnit.SECONDS.sleep(2);

        //内容行
        List orders = new ArrayList();
        List<WebElement> list = driver.findElements(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div > div.SK-theme-block-content > div.SK-my-order-container"));
        for (WebElement webElement : list) {
            Order order = new Order();
            String name = webElement.findElement(By.cssSelector("div.row1 > div > div > div")).getText();
            String date = webElement.findElement(By.cssSelector("div.row2")).getText();
            String orderId = webElement.findElement(By.cssSelector("div.row3")).getText();
            String prices = webElement.findElement(By.cssSelector("div.row4")).getText();
            String status = webElement.findElement(By.cssSelector("div.row5 > div")).getText();
            String link = webElement.findElement(By.cssSelector("div.row5 > a")).getAttribute("href");

            order.setName(name);
            order.setDate(date);
            order.setOrderId(orderId);
            order.setPrices(prices);
            order.setStatus(status);
            order.setLink(link);

            orders.add(order);
        }

        orders.forEach(System.out::println);
        orders.stream().forEach(o -> logger.debug(o.toString()));
        return orders;
    }

    private Capabilities iniChromeOptions() {
        System.setProperty("webdriver.chrome.driver", "D:\\env\\dcm\\2.37\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        //后续处理IE下面下载问题
        String userAgentIE11 = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
        chromePrefs.put("profile.general_useragent_override", userAgentIE11);
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("profile.managed_default_content_settings.images", 2);
        //对于实验性质的选项
        options.setExperimentalOption("prefs", chromePrefs);
        //消除安全校验
        options.addArguments("--allow-running-insecure-content");
        //最大化
        options.addArguments("--ash-enable-fullscreen-app-list");
        //启动最大化，防止失去焦点
        options.addArguments("--start-maximized");
        //关闭gpu图片渲染
        options.addArguments("--disable-gpu");
        //无界面浏览器
//        options.setHeadless(true);
        //user-agent 设置 profile.general_useragent_override

        //以下打开日志
        /*LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.CLIENT, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        logs.enable(LogType.PERFORMANCE, Level.ALL);
        logs.enable(LogType.PROFILER, Level.ALL);
        logs.enable(LogType.SERVER, Level.ALL);
        options.setCapability(CapabilityType.LOGGING_PREFS,logs);
        options.setCapability(CapabilityType.ENABLE_PROFILING_CAPABILITY,true);*/

        //webDriver错误弹框，同意,貌似没啥用
        options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
        return options;

    }

    private void login(WebDriver driver) throws Exception {
        driver.get("https://www.sonkwo.com/");
        //等待5s
        TimeUnit.SECONDS.sleep(5);
        //进入登录界面
        driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-store-header-container > div.SK-store-header-content > div > div.links > a:nth-child(1)")).click();
        TimeUnit.SECONDS.sleep(2);

        driver.findElement(By.cssSelector("#login_name")).sendKeys(new String[]{"uamivtco@10mail.org"});
        TimeUnit.SECONDS.sleep(2);

        driver.findElement(By.cssSelector("#password")).sendKeys(new String[]{"uamivtco@10mail.org"});
        TimeUnit.SECONDS.sleep(2);

        driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-login-background > div > div > div > div > div > div.SK-form-body-wrapper > div > div.SK-button-com.submit-btn > button")).click();
        TimeUnit.SECONDS.sleep(2);
    }
}
