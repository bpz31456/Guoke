package cn.ms22.image;

import cn.ms22.basic.AbstractDriver;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ImageTest extends AbstractDriver {

    @Test
    public void test() {
        fetchVerificationCode("E:\\tensorflow\\input_data\\", 100, 10);
    }

    private void fetchVerificationCode(String savePath, int count, int groupCount) {
        WebDriver webDriver = new ChromeDriver(iniChromeOptions());
        webDriver.get("https://www.sonkwo.com/sign_up");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement img = webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(3) > div > div"));
        Rectangle rect = img.getRect();
        try {
            int group = -1;
            for (int i = 0; i < count; i++) {
                if (i % groupCount == 0) {
                    group++;
                }
                img.click();
                TimeUnit.MILLISECONDS.sleep(500);
                File source = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
                try {
                    BufferedImage bufImage = ImageIO.read(source);
                    BufferedImage codeImage = bufImage.getSubimage(rect.getX() + 1, rect.getY() + 1, rect.getWidth(), rect.getHeight());
                    String filepath = savePath + group;
                    if (Files.notExists(Paths.get(filepath))) {
                        Files.createDirectories(Paths.get(filepath));
                    }
                    ImageIO.write(codeImage, "png", new File(filepath + "\\" + i + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                img = webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(3) > div > div"));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            webDriver.close();
        }
    }

    @Test
    public void imgToBase64(){
        String imgPath = "E:\\tensorflow\\input_data\\3.png";
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        logger.debug("本地图片转换Base64:" + encoder.encode(Objects.requireNonNull(data)));

    }


}
