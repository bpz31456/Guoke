package cn.ms22.driver;

import cn.ms22.config.ApplicationConfig;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class AbstractDriver {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 内置驱动，close关闭页面，quit退出窗口
     */
    protected WebDriver webDriver = new ChromeDriver(this.iniChromeOptions(ApplicationConfig.getInstance().getDriver()));

    protected Set<String> handlers = new HashSet<>();

    protected void sleepSec(int sec) {
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    protected void sleepMiSec(int miSec) {
        try {
            TimeUnit.MILLISECONDS.sleep(miSec);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    protected void openOtherPage(String pageAddress) {
        String skipDropmailJs = "window.open('" + pageAddress + "');";
        ((ChromeDriver) webDriver).executeScript(skipDropmailJs);
        sleepSec(1);
        handlers = webDriver.getWindowHandles();
    }

    protected void openPage(String pageAddress) {
        webDriver.get(pageAddress);
        handlers = webDriver.getWindowHandles();
    }

    protected void changeDoublePage() {
        switch (handlers.size()) {
            case 0:
            case 1:
                break;
            case 2:
                for (String handler : handlers) {
                    if (!webDriver.getWindowHandle().equals(handler)) {
                        webDriver.switchTo().window(handler);
                        break;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("多余两个页面需要跳转。");
        }
    }

    protected ChromeOptions iniChromeOptions(String driver) {
        System.setProperty("webdriver.chrome.driver", driver);
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> chromePrefs = new HashMap<>();
        //后续处理IE下面下载问题
        String userAgentIE11 = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";
        chromePrefs.put("profile.general_useragent_override", userAgentIE11);
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("profile.managed_default_content_settings.images", 1);
        //对于实验性质的选项
        options.setExperimentalOption("prefs", chromePrefs);
        //消除安全校验
        options.addArguments("--allow-running-insecure-content");
        //最大化
        options.addArguments("--ash-enable-fullscreen-app-list");
        //启动最大化，防止失去焦点
        options.addArguments("--start-maximized");
        //关闭gpu图片渲染
//        options.addArguments("--disable-gpu");
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
}
