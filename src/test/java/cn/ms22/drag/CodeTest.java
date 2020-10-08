package cn.ms22.drag;

import cn.ms22.basic.AbstractDriver;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.concurrent.TimeUnit;

/**
 * 验证码拖动
 */
public class CodeTest extends AbstractDriver {
    @Test
    public void test() throws InterruptedException {
        WebDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        webDriver.get("https://www.sonkwo.com/sign_up");
        webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > ul > li:nth-child(2)")).click();
        TimeUnit.SECONDS.sleep(1);
//        WebElement scrollBar = webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(6) > div > div > div > span"));
//        Actions actions = new Actions(webDriver);
        TimeUnit.SECONDS.sleep(5);
//        actions.clickAndHold(scrollBar).moveByOffset(410,0);
        logger.debug("开始拖动。");
        TimeUnit.SECONDS.sleep(2);
//        actions.moveToElement(scrollBar).release();
//        actions.build().perform();
        logger.debug("拖动完毕。");
        TimeUnit.SECONDS.sleep(20);
        webDriver.close();
        webDriver.quit();
    }
}
