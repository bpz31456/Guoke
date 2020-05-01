package cn.ms22.task;

import cn.ms22.config.ApplicationConfig;
import cn.ms22.driver.AbstractDriver;
import cn.ms22.exception.TimeoutException;
import cn.ms22.persistence.DataPersistence;
import cn.ms22.persistence.DataPersistenceFactory;
import cn.ms22.pool.CatchThreadPoolFactory;
import cn.ms22.pool.DoubleCodeCache;
import cn.ms22.utils.DateTools;
import cn.ms22.utils.FileTools;
import org.openqa.selenium.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author baopz
 */
public class RegisterTask extends AbstractDriver implements Runnable {
    private String lastEmail;
    private String lastCode;
    private DoubleCodeCache codeCache = new DoubleCodeCache();
    private String lastWebCode;
    private String lastShotName = "tmp.png";
    private static final String SHOT = "SHOT";

    @Override
    public void run() {
        try {
            run0();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }

    private void run0() throws Exception {
        //打开注册页面
        String registerAddress = "https://www.sonkwo.com/sign_up";
        openPage(registerAddress);
        addConfirmButton();
        //跳转邮箱注册
        toEmailRegisterAnchor();
        //跳转邮件页面
        String emailAddress = "https://dropmail.me/zh/";
        openOtherPage(emailAddress);
        //
        boolean isFirstTime = true;

        while (true) {
            changeDoublePage();
            //获取邮件地址
            if (isFirstTime) {
                sleepSec(5);
                webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(2) > div.row.toolbar > div > div:nth-child(1) > button.btn.btn-default.btn-sm.dropdown-toggle")).click();
                webDriver.findElement(By.cssSelector("#dropdown > li:nth-child(4)")).click();
                isFirstTime = false;
            }
            fetchEmail();
            //跳转注册页面，录入邮件地址，发送邮件验证码
            changeDoublePage();
            sendEmail();
            //跳转邮箱页面，获取邮件验证码
            changeDoublePage();
            fetchCode();
            //跳转注册页面，录入邮箱验证码
            changeDoublePage();
            //录入页面验证码
            inputCode();
            focusHanWriteInput();
            //轮询校验
            try {
                suspend();
                //持久化数据
                DataPersistence dataPersistence = DataPersistenceFactory
                        .getTxtDataPersistenceInstance(ApplicationConfig.getInstance().getOutput() + File.separator + DateTools.currentDate() + "password" + FileTools.FILE_TYPE_TXT);
                dataPersistence.saveAccount(lastEmail);
                saveShot(ApplicationConfig.getInstance().getOutput() + File.separator + SHOT + File.separator + lastShotName,
                        ApplicationConfig.getInstance().getOutput() + File.separator + SHOT + File.separator + DateTools.currentTime() + lastWebCode + ".png");
                logger.info("获取验证码:{}", lastWebCode);
            } catch (TimeoutException e) {
                logger.warn(e.getMessage());
            }
            //刷新页面
            clearInfo();
            sleepSec(1);
        }
    }

    private void suspend() throws TimeoutException {
        ThreadPoolExecutor suspend = CatchThreadPoolFactory.getSingleInstance("suspend", "停顿线程-等待页面操作完毕。");
        Future<Boolean> submit = suspend.submit(new SuspendTask());
        try {
            if (!submit.get()) {
                throw new TimeoutException("超过最大尝试次数。");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new TimeoutException("超过最大尝试次数");
        }
    }

    private void focusHanWriteInput() {
        WebElement captchaInput = webDriver.findElement(By.cssSelector("#captcha"));
        captchaInput.clear();
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].focus()", captchaInput);
        screenshot(ApplicationConfig.getInstance().getOutput() + File.separator + SHOT + File.separator + lastShotName);
    }

    private void focusEmailInput() {
        WebElement emailToken = webDriver.findElement(By.cssSelector("#pending_email_token"));
        emailToken.clear();
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].focus()", emailToken);
    }

    private class SuspendTask implements Callable<Boolean> {
        private boolean isConfirm = false;

        @Override
        public Boolean call() {
            int useTimes = 0;
            int maxTryTimes = 200;
            while (!isConfirm && useTimes <= maxTryTimes) {
                try {
                    //手写验证码错误
                    WebElement element = webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(3) > div.text-hint.error-hint"));
                    String handWriteCodeErrorMsg = element.getText();
                    String HAND_WRITING_CODE_ERROR_MSG = "验证码错误";
                    if (handWriteCodeErrorMsg != null && handWriteCodeErrorMsg.contains(HAND_WRITING_CODE_ERROR_MSG)) {
                        focusHanWriteInput();
                    }
                } catch (Exception e) {
                    //ignore
                }

                //邮箱验证码错误
                try {
                    WebElement element = webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(4) > div.text-hint.error-hint"));
                    String emailCodeErrorMsg = element.getText();
                    String EMAIL_CODE_ERROR_MSG = "验证码错误";
                    if (emailCodeErrorMsg != null && emailCodeErrorMsg.contains(EMAIL_CODE_ERROR_MSG)) {
                        focusEmailInput();
                    }
                } catch (Exception e) {
                    //ignore
                }

                //获取验证码
                try {
                    lastWebCode = webDriver.findElement(By.cssSelector("#captcha")).getAttribute("value");
                } catch (Exception e) {
                    //ignore
                }

                //正确提示校验
                try {
                    WebElement element = webDriver.findElement(By.cssSelector("#root > div > div.SK-notify-messages.headlines > div > div > div"));
                    String successMsg = element.getText();
                    String SUCCESS_MSG = "验证码错误";
                    if (successMsg != null && successMsg.contains(SUCCESS_MSG)) {
                        isConfirm = true;
                    }
                } catch (Exception e) {
                    //ignore
                }


                //正确提示校验
                try {
                    if (webDriver.getCurrentUrl().contains("sign_in")) {
                        isConfirm = true;
                    }
                } catch (Exception e) {
                    //ignore
                }
                sleepMiSec(500);
                useTimes++;
            }
            isConfirm = false;
            return useTimes < maxTryTimes;
        }
    }

    private void saveShot(String from, String target) {
        Path path = Paths.get(from);
        boolean b = path.toFile().renameTo(new File(target));
        if (b) {
            logger.debug("报错文件成功：{}", target);
        } else {
            logger.warn("报错文件失败:{}", target);
        }
    }

    private void addConfirmButton() {
        addEnterEvent();
    }

    /**
     * 添加快照按钮
     * 添加提交申请按钮
     */
    private void addEnterEvent() {
        ((JavascriptExecutor) webDriver).executeScript(
                "document.onkeydown=function()" +
                        "{if(window.event.keyCode == 13)" +
                        "{document.querySelector('#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(7) > div.SK-button-com.submit-btn > button').click();}};");
    }

    private void clearInfo() {
        ((JavascriptExecutor) webDriver).executeScript("window.location.assign('https://www.sonkwo.com/sign_up');");
        sleepMiSec(700);
        addConfirmButton();
        //跳转邮箱注册
        toEmailRegisterAnchor();
    }

    private void inputCode() {
        webDriver.findElement(By.cssSelector("#pending_email_token")).sendKeys(lastCode);
    }

    private void fetchCode() {
        String curCode = null;
        int pauseCount = 10;
        int curCount = 0;
        lastCode = null;
        while ((lastCode == null || curCode == null || lastCode.equals(curCode)) && curCount < pauseCount) {
            try {
                WebElement messageEle;
                if (lastCode == null) {
                    messageEle = webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(6) > ul > li > div:nth-child(2) > div:nth-child(2) > pre"));
                    String preMsg = messageEle.getText();
                    curCode = preMsg.split("\\n")[1].trim();
                    if (!codeCache.contains(curCode)) {
                        lastCode = curCode;
                        codeCache.add(curCode);
                        break;
                    }
                } else {
                    messageEle = webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(6) > ul > li:nth-child(1) > div:nth-child(2) > div:nth-child(2) > pre"));
                    String preMsg = messageEle.getText();
                    curCode = preMsg.split("\\n")[1].trim();
                    if (!lastCode.equals(curCode) && !codeCache.contains(curCode)) {
                        lastCode = curCode;
                        codeCache.add(curCode);
                        break;
                    }
                }
            } catch (Exception e) {
                //ignore
            }
            sleepMiSec(500);
            curCount++;
        }
    }

    private void sendEmail() {
        webDriver.findElement(By.cssSelector("#email")).sendKeys(lastEmail);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(lastEmail);
        webDriver.findElement(By.cssSelector("#password_confirmation")).sendKeys(lastEmail);
        webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(4) > div:nth-child(2) > div > button")).click();
        sleepMiSec(500);
    }

    /**
     * 获取email
     */
    private void fetchEmail() {
        //生成
        webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(2) > div.row.toolbar > div > div:nth-child(1) > button:nth-child(1)")).click();
        //条件判断
        try {
            webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(2) > div.alert.alert-warning > div > input:nth-child(1)")).click();
        } catch (Exception sube) {
            //ignore
        }
        sleepMiSec(700);
        decodingEmail();
        while ("1".equals(lastEmail)) {
            decodingEmail();
        }
    }

    private void decodingEmail() {
        String text = webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(2) > div.well.well-sm.text-center")).getText();
        String[] subEmails = text.split("\\s");
        lastEmail = subEmails[subEmails.length - 1];
        logger.debug("获取邮箱信息:{}", lastEmail);
    }

    private void toEmailRegisterAnchor() {
        webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > ul > li:nth-child(2)")).click();
    }

    private void screenshot(String filepath) {
        WebElement img = webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(3) > div > div"));
        Rectangle rect = img.getRect();
        File source = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        try {
            BufferedImage bufImage = ImageIO.read(source);
            BufferedImage codeImage = bufImage.getSubimage(rect.getX() + 1, rect.getY() + 1, rect.getWidth(), rect.getHeight());
            if (Files.notExists(Paths.get(filepath))) {
                Files.createDirectories(Paths.get(filepath));
            }
            ImageIO.write(codeImage, "png", new File(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Share {
        private String lastWebCode;

        public String getLastWebCode() {
            return lastWebCode;
        }

        public void setLastWebCode(String lastWebCode) {
            this.lastWebCode = lastWebCode;
        }
    }
}
