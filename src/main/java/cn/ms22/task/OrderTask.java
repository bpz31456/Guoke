package cn.ms22.task;

import cn.ms22.config.ApplicationConfig;
import cn.ms22.entity.*;
import cn.ms22.persistence.DataPersistence;
import cn.ms22.persistence.QueueL;
import cn.ms22.persistence.QueueLR;
import cn.ms22.utils.FileTools;
import org.openqa.selenium.By;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author baopz
 */
public class OrderTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(OrderTask.class);
    private static String driverConfig = ApplicationConfig.getInstance().getDriver();
    private String username;
    private String password;

    private Semaphore semaphore;
    private DataPersistence dataPersistence;

    private CountDownLatch countDownLatch;

    public OrderTask(String username,
                     String password,
                     Semaphore semaphore,
                     DataPersistence dataPersistence,
                     CountDownLatch countDownLatch) {
        this.username = username;
        this.password = password;
        this.semaphore = semaphore;
        this.dataPersistence = dataPersistence;
        this.countDownLatch = countDownLatch;
    }

    public OrderTask(String username, String password, Semaphore semaphore, DataPersistence dataPersistence) {
        this.username = username;
        this.password = password;
        this.semaphore = semaphore;
        this.dataPersistence = dataPersistence;
    }

    @Override
    public void run() {
        WebDriver driver = null;
        try {
            semaphore.acquire();
            driver = new ChromeDriver(iniChromeOptions());
            run1(driver);
        } catch (Exception e) {
            //失败数据收集。
            PassWord passwd = new PassWord();
            passwd.setPassword(password);
            passwd.setUsername(username);
            QueueLR.getInstance().add(passwd);
            logger.error("帐号{}，失败原因:{}", username, e.getMessage());
        } finally {
            semaphore.release();
            logger.debug("semaphore释放。");
            countDownLatch.countDown();
            if (driver != null) {
                driver.close();
            }
        }
    }

    /**
     * @since version 0.1.2
     * @param driver
     * @throws Exception
     */
    private void run1(WebDriver driver) throws Exception {
        logger.info("用户【{}】进入混合站点", username);
        login(driver, QueueL.MIXTURE);
        mixtureDetail(driver);
    }

    private void mixtureDetail(WebDriver driver) throws Exception {
        //国内站点
        logger.info("用户{}进入混合站点国内分站点", username);
        TimeUnit.SECONDS.sleep(1);
        driver.get("https://www.sonkwo.com/setting/game_factory?area=cn");
        TimeUnit.SECONDS.sleep(1);
        List<CodeOrder> codeOrders = mixtureAnalysisDetail(driver);
        logger.debug("混合站点国内分站点获取数据{}",codeOrders.size());

        //国际站点
        logger.info("用户{}进入混合站点国际分站点", username);
        driver.get("https://www.sonkwo.com/setting/game_factory?area=hk");
        TimeUnit.SECONDS.sleep(1);
        List<CodeOrder> codeOrders1 =  mixtureAnalysisDetail(driver);
        logger.debug("混合站点国际分站点获取数据{}",codeOrders1.size());
        codeOrders.addAll(codeOrders1);

        logger.debug("混合站点国际站点总数据{}",codeOrders.size());
        //输出
        try {
            if (dataPersistence != null) {
                logger.info("用户【{}】混合站点开始输出。", username);
                dataPersistence.save(codeOrders, username + QueueL.MIXTURE + FileTools.FILE_TYPE_TXT);
                QueueL.getInstance(QueueL.MIXTURE).addAll(codeOrders);
            }
        } catch (IOException e1) {
            logger.error("持久化失败"+e1.getMessage());
            throw e1;
        }
        logger.info("用户【{}】混合站点输出完成。", username);
    }

    private List<CodeOrder> mixtureAnalysisDetail(WebDriver driver) throws Exception {
        List<CodeOrder> codeOrders = new ArrayList<>();
        //处理分页信息
        WebElement pageInfo = null;
        boolean hasNext = false;
        try {
            pageInfo = driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.SK-pagination-container"));
            hasNext = true;
        } catch (Exception e) {
            //ignore Don't have pageInfo
            logger.info("没有分页信息");
        }
        mixtureOnePageDetail(driver, codeOrders);
        while (hasNext) {
            //点击下一页
            try {
                pageInfo.findElement(By.cssSelector("button:last-child")).click();
                TimeUnit.MILLISECONDS.sleep(500);
                mixtureOnePageDetail(driver, codeOrders);
            } catch (Exception e) {
                logger.warn("最后一页结束");
                hasNext = false;
            }
        }
        return codeOrders;
    }

    /**
     * 增加扩展
     * @since 0.3.2
     * @param driver
     * @param codeOrders
     * @throws InterruptedException
     */
    private void mixtureOnePageDetail(WebDriver driver, List<CodeOrder> codeOrders) throws InterruptedException {
        List<WebElement> elements = driver.findElements(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div.game-factory-list-container "));
        for (int i = 1; i <= elements.size(); i++) {
            WebElement tmp = elements.get(i - 1);
            List<WebElement> webElements = tmp.findElements(By.cssSelector("div.game-list"));
            for (WebElement element : webElements) {
                //点击列表中某一个
                String name = element.findElement(By.cssSelector("div.game-list-left > div")).getText();
                String platform = element.findElement(By.cssSelector("div.game-type")).getText();
                element.findElement(By.cssSelector("div.btn.steam-key")).click();

                TimeUnit.MILLISECONDS.sleep(1000);
                List<WebElement> elementList = driver.findElements(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div:nth-child(" + i + ") > div.overlayfixed.undefined > div > div > div.overlaybox-body > div > div.game-key-list > div.game-key-container"));
                logger.info(name + elementList.size() + "");
                if(elementList.size()==0){
                    continue;
                }else{
                    //取本体
                    String code = elementList.get(0).findElement(By.cssSelector("div.key-code")).getText();
                    CodeOrder codeOrder = CodeOrderFactory.create(username,name,code,platform);
                    if(codeOrders.contains(codeOrder)){
                        throw new InterruptedException("已经获取最后一页数据，分页获取完成。");
                    }
                    //设置dlc
                    List<String> codeExs = new ArrayList<>();
                    for (int j = 1; j < elementList.size(); j++) {
                        code = elementList.get(j).findElement(By.cssSelector("div.key-code")).getText();
                        codeExs.add(code);
                    }
                    codeOrder.setCodeExs(codeExs);
                    //添加
                    codeOrders.add(codeOrder);
                }
                TimeUnit.MILLISECONDS.sleep(500);
                driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div:nth-child(" + i + ") > div.overlayfixed.undefined > div > div > div.overlaybox-body > div > h5 > div > i")).click();

                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    @Deprecated
    private void run0(WebDriver driver) throws Exception {
        //登录
        logger.info("用户【{}】进入国内站点", username);
        login(driver, QueueL.INNER_QUEUE);
        //国内详情获取
        innerDetail(driver);

        logger.info("用户【{}】进入国际站点", username);
        login(driver, QueueL.OUTER_QUEUE);
        //国外详情获取
        outerDetail(driver);
    }

    /**
     * 国际站
     *
     * @param driver
     * @throws Exception
     */
    @Deprecated
    private void outerDetail(WebDriver driver) throws Exception {
        TimeUnit.SECONDS.sleep(2);
        driver.get("http://www.sonkwo.hk/setting/game_factory");
        TimeUnit.SECONDS.sleep(2);

        List<CodeOrder> codeOrders = singleAnalysisDetail(driver);
        //输出
        if (dataPersistence != null) {
            logger.info("用户【{}】国际站激活码数据本地化存储。", username);
            dataPersistence.save(codeOrders, username + QueueL.OUTER_QUEUE + FileTools.FILE_TYPE_TXT);
            QueueL.getInstance(QueueL.OUTER_QUEUE).addAll(codeOrders);
        }
        logger.info("用户【{}】国际站获取激活码完成。", username);
    }

    /**
     * 国内站
     *
     * @param driver
     * @throws Exception
     */
    @Deprecated
    private void innerDetail(WebDriver driver) throws Exception {
        TimeUnit.SECONDS.sleep(2);
        driver.get("https://www.sonkwo.com/setting/game_factory");
        TimeUnit.SECONDS.sleep(2);

        List<CodeOrder> codeOrders = singleAnalysisDetail(driver);
        //输出
        if (dataPersistence != null) {
            logger.info("用户【{}】国内站激活码数据本地化存储。", username);
            dataPersistence.save(codeOrders, username + QueueL.INNER_QUEUE + FileTools.FILE_TYPE_TXT);
            QueueL.getInstance(QueueL.INNER_QUEUE).addAll(codeOrders);
        }
        logger.info("用户【{}】国内站获取激活码完成。", username);
    }

    @Deprecated
    private List<CodeOrder> singleAnalysisDetail(WebDriver driver) throws InterruptedException {
        List<CodeOrder> codeOrders = new ArrayList<>();
        //处理分页信息
        WebElement pageInfo = null;
        boolean hasNext = false;
        try {
            pageInfo = driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.SK-pagination-container"));
            hasNext = true;
        } catch (Exception e) {
            //ignore Don't have pageInfo
            logger.debug("没有分页信息");
        }
        onePageDetail(driver, codeOrders);
        while (hasNext) {
            //点击下一页
            try {
                pageInfo.findElement(By.cssSelector("button:last-child")).click();
                TimeUnit.MILLISECONDS.sleep(500);
                onePageDetail(driver, codeOrders);
            } catch (Exception e) {
                logger.warn("没有最后一页");
                hasNext = false;
            }
        }
        return codeOrders;
    }

    /**
     * 一页的详细信息获取
     *
     * @param driver
     * @param codeOrders
     * @throws InterruptedException
     */
    @Deprecated
    private void onePageDetail(WebDriver driver, List<CodeOrder> codeOrders) throws InterruptedException {
        List<WebElement> elements = driver.findElements(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div.game-factory-list-container "));
        for (int i = 1; i <= elements.size(); i++) {
            WebElement tmp = elements.get(i - 1);
            List<WebElement> webElements = tmp.findElements(By.cssSelector("div.game-list"));
            for (WebElement element : webElements) {
                String name = element.findElement(By.cssSelector("div.game-list-left > div")).getText();
                String platform = element.findElement(By.cssSelector("div.game-type")).getText();
                element.findElement(By.cssSelector("div.btn.steam-key")).click();

                TimeUnit.MILLISECONDS.sleep(300);
                List<WebElement> elementList = driver.findElements(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div:nth-child(" + i + ") > div.overlayfixed.undefined > div > div > div.overlaybox-body > div > div"));
                logger.info(name + elementList.size() + "");
                for (WebElement webElement : elementList) {
                    CodeOrder codeOrder = new CodeOrder();
//                    TimeUnit.MILLISECONDS.sleep(500);
                    String code = webElement.findElement(By.cssSelector("div.key-code")).getText();

                    codeOrder.setUsername(username);
                    codeOrder.setName(name);
                    codeOrder.setPlatform(platform);
                    codeOrder.setCode(code);
                    codeOrders.add(codeOrder);
                }

                TimeUnit.MILLISECONDS.sleep(500);
                driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-community-content > div > div.SK-tabs-content.SK-user-center-tabs-content > div > div.gamefactory-game-container > div:nth-child(" + i + ") > div.overlayfixed.undefined > div > div > div.overlaybox-body > div > h5 > div > i")).click();

                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

    /**
     * 数据不全，被遗弃
     *
     * @param driver
     * @param orders
     * @return
     * @throws InterruptedException
     */
    @Deprecated
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

    /**
     * 获取数据不全，被遗弃
     *
     * @param driver
     * @return
     * @throws InterruptedException
     */
    @Deprecated
    private List toOrdersPage(WebDriver driver) throws InterruptedException {
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

    /**
     * chrome初始化
     *
     * @return
     */
    private ChromeOptions iniChromeOptions() {
        System.setProperty("webdriver.chrome.driver", driverConfig);
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromePrefs = new HashMap<>();
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
        //无界面浏览器，无法点击
//        options.addArguments("--headless");
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

    private void login(WebDriver driver, String place) throws Exception {
        switch (place) {
            case QueueL.INNER_QUEUE:
            case QueueL.MIXTURE:
                driver.get("https://www.sonkwo.com/sign_in");
                break;
            case QueueL.OUTER_QUEUE:
                driver.get("http://www.sonkwo.hk/");
                break;
            default:
                logger.error("未知的登录位置，{}", place);
        }
        //等待5s
        TimeUnit.SECONDS.sleep(2);

        driver.findElement(By.cssSelector("#login_name")).sendKeys(new String[]{username});
        TimeUnit.MILLISECONDS.sleep(300);

        driver.findElement(By.cssSelector("#password")).sendKeys(new String[]{password});
        TimeUnit.MILLISECONDS.sleep(300);

        driver.findElement(By.cssSelector("#content-wrapper > div > div.SK-login-background > div > div > div > div > div > div.SK-form-body-wrapper > div > div.SK-button-com.submit-btn > button")).click();
        TimeUnit.SECONDS.sleep(1);
    }
}
