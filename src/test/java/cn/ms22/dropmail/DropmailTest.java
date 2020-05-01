package cn.ms22.dropmail;


import cn.ms22.basic.AbstractDriver;
import cn.ms22.task.TaskFactory;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class DropmailTest extends AbstractDriver {

    @Test
    public void test() {
        WebDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            webDriver.get("https://dropmail.me/zh/");
            //下拉菜单
            webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(2) > div.row.toolbar > div > div:nth-child(1) > button.btn.btn-default.btn-sm.dropdown-toggle")).click();
            webDriver.findElement(By.cssSelector("#dropdown > li:nth-child(4)")).click();

            //生成
            for (int i = 0; i < 10; i++) {
                webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(2) > div.row.toolbar > div > div:nth-child(1) > button:nth-child(1)")).click();
                //条件判断
                try {
                    webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(2) > div.alert.alert-warning > div > input:nth-child(1)")).click();
                } catch (Exception sube) {
                    //ignore
                }
            }
            super.sleepSec(2);
            String text = webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(2) > div.well.well-sm.text-center")).getText();
            logger.debug("获取信息:{}", text);
            String[] subEmails = text.split("\\s");
            String registerEmail = subEmails[subEmails.length - 1];
            logger.debug("获取信息:{}", registerEmail);
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.close();
        }
    }

    @Test
    public void enterTest() {
        WebDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            //注册页面
            webDriver.get("https://www.sonkwo.com/sign_up");
            String rst = (String) ((JavascriptExecutor) webDriver).executeScript(
                    "document.onkeydown=function()" +
                            "{return 'abc';};");
            if (rst != null) {
                logger.debug("成功");
            }
            sleepSec(20);
        } catch (Exception e) {
            //ignore
        } finally {
            webDriver.close();
        }
    }

    @Test
    public void windowsChangeTest() {
        WebDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            //注册页面
            webDriver.get("https://www.sonkwo.com/sign_up");
            //记录handler
            super.handlers.add(webDriver.getWindowHandle());
            sleepSec(2);
            String skipDropmailJs = "window.open(\"https://dropmail.me/zh/\");";
            ((ChromeDriver) webDriver).executeScript(skipDropmailJs);
            sleepSec(2);
            super.handlers.add(webDriver.getWindowHandle());
            sleepSec(2);
            //转换
            webDriver.switchTo().window(super.handlers.get(0));
            sleepSec(2);
            webDriver.switchTo().window(super.handlers.get(1));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
    }

    @Test
    @Deprecated
    public void fetch() {
        WebDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        //邮件名
        WebElement emailEle = webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(6) > ul > li > div:nth-child(2) > dl > dd:nth-child(4) > span"));
        String email = emailEle.getText();

        //
        WebElement messageEle = webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(6) > ul > li > div:nth-child(2) > div:nth-child(2) > pre"));
        String preMsg = messageEle.getText();

        String msg = preMsg.split("\\n")[1];
        logger.debug("msg:{}", msg);
    }

    @Test
    public void fetch0() {
        TaskFactory.getRegisterTask().run();
    }

    @Test
    public void js2() {
        ChromeDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            webDriver.get("https://www.sonkwo.com/sign_up");
            WebElement element = webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(3) > div"));
            webDriver.executeScript("var buttonNode = document.createElement('button');" +
                    "buttonNode.onclick=function(){alert('1234');};" +
                    "var buttonText = document.createTextNode('确定');" +
                    "buttonNode.appendChild(buttonText);" +
                    "arguments[0].appendChild(buttonNode);", element);
            sleepSec(20);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
    }

    @Test
    public void buttonDown() {
        ChromeDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            webDriver.get("https://www.sonkwo.com/sign_up");
            sleepSec(5);

            webDriver.executeScript("document.onkeydown=function(){if(window.event.keyCode == 13){document.querySelector('#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(7) > div.SK-button-com.submit-btn > button').click()}};");
            //#content-wrapper > div > div.sign-up-panel > div > div > div > div:nth-child(7) > div.SK-button-com.submit-btn > button
            /*webDriver.executeScript("document.onkeydown = function(e){" +
                    "var theEvent = window.event||e;" +
                    "var code = theEvent.keyCode || theEvent.which;" +
                    "if (code == 13) {alert('1234');}");*/
            sleepSec(10);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            webDriver.close();
        }

    }

    @Test
    public void js1() {
        String imgBase641 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAKAAAAAoCAYAAAB5LPGYAAAFCklEQVR42u2cvUosQRCF9xUEQ9HA\n" +
                "UDDwHQTfwlwwMDfwDQxMxUAw9y3MzQ1Ew82MRGQuZ+DIuUX138zOuu5WQXNnd2Z7eqq+qaqubu+s\n" +
                "Cwn5RZmFCkICwJAAMCQkAAwJAENCAsCQADDEl/l83p2dnfUNxyEB4OTy8vLSPTw8dPv7+93W1tZP\n" +
                "e319DeUEgNMKIFPo2ADj+/v7KE/69fXVH398fPT34ecAMORHDg4OXADhEYfK4+Njt7Oz00MHiAHz\n" +
                "zc1NABhSH4JTAMKz3d/f9w15Ysmjos/r6+vu6OhoI0N6ANggh4eHRQAtYPiNN4kBcNarBoAh2ZxN\n" +
                "w3EKQAvVycmJe93l5WW3vb0dAAZadQKvVcoBARsgQj7H62o9agC4xuBo8zybFxIBE74/PT3NTkKY\n" +
                "8z09PfXf6TUAMQBcUwBp8Lu7ux4ACr47Pz93obE5GWaj9jxmpwAP/SNM4hgwpgD0yjS4npOWAPCP\n" +
                "Awij6IqDDXHIy/gZxzbnQmkDv7m4uOgNzvM2p+Pvh4RggIy+LVDsE/dnCwD/AIDWoAydnidCQ5hM\n" +
                "FXIJIIq97MMDqgRATQ7oTUI8DwzPWgMgxpqaxASAI8MnczKbM+Ff5GOojcHIaLieBmOIZY4Gz8bz\n" +
                "KQ+K/ng/GN/majWF5VoAGZJT5/Fy5TwgPDOeDc+IZ8PLxZA+pvgdAJqciwDRG7A0AU9FZbPwq5MH\n" +
                "GMmbTOQAtB7IAljT31APmBM8r+cl6fkAHyDUnFb1FQA6wpBnm51Nwtt518Gb8TzyJzQFpMXAHoAw\n" +
                "LJfCNIQzRI+dNLSOz0sDtOVSDr0/PCe/+0u7dBYKIIzrrZ2qwko5km3Hx8f/KbTVwHY8rR5t2QCW\n" +
                "mk5qcMxcWV8oHPP7Vc8hRwPInEUNqTmZzcE0B6rxWBZk+7nVwKsCIEpHCk0tsCwTpfYjagpjUw3o\n" +
                "S2uiq+ApRwHInA6ApSYCWjIZEjJZt0sZjGEVGwZaAGR/Or5S4bgGML4kGBP61xcOx1qnZGTQ/ko5\n" +
                "nuaENePV51X4WHgvOYSVAhBbh2wdruTibUhoBTDngQA9tzPp5xZg7P1Ke/xy4+MM34ZM1Rf1QMOX\n" +
                "+mu9f2vKVDsJ+1UAWZfTxXNOImo8RssDpnI2D0CeR9ih58EYaeTU9TmD4vc5L2THx0lN6n4lff0m\n" +
                "gFoSW0kAYQgaFm6/tQ41pNKfA8b2B6NaWDT/oWdEuPHWepkDIXzbIreWiVJ1S23Uj005SinKGABb\n" +
                "QnANiNAXopUubS4dQHgTrTtB6UMHNAWAqUmE7Z/noVCFK5eE6wvX2qAzlnJgTAs8znsbWgksc8YW\n" +
                "/UyxdAe9XF1dTVb4tmv1M5uz0GiLSE5ZltE6YC7HKoVMe55liJrirO6/07JQrmm41b8BgW5q9IPn\n" +
                "13SADWPhWrTqJ7cWrR4Q0GI8eJ6pxNuv6NUpS83yxRSJ+pt5Blnk9FzDBRTs5Vi1OSau0XG2/g0F\n" +
                "APJeuFzjvWoK1a3lK1t4R82zNKnjeDAbnhJAnXR6L2RLU76sA5p8Kc7mLJojsS6lLp/hkWvBmyLc\n" +
                "XpYro2DrGVpuf+PGL8WV6lAIHVA0oFNFcv9drUE2Ubg/UV/QmtJWADggZC+qzLDugpCmOVUAuEAA\n" +
                "4QXXfT/cpsvKe0Bdiov/iyUAXDqALOTGfwYUAE4i39/f3e3tbQ/a7u5ut7e399Pe3t7CQgHgtPL5\n" +
                "+dk9Pz/3DcchAWBIyNLkH086z0ZjJX2QAAAAAElFTkSuQmCC";
        String imgBase642 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAKAAAAAoCAYAAAB5LPGYAAAE+UlEQVR42u2cvUozQRSGcwuKpWBh\n" +
                "KVh4D4I3YG0vWIithZWthb2FYGXjXQjeg4VgaWdpsR/PwhvOd5y/JLtqknNgMMnOzm5mnj3nzDsT\n" +
                "J11Y2C/aJLogLAAMCwDDwgLAsAAwLCwADAsAw37eLi8vu9PT02/l/v4+AAwbz15fX7ujo6NuY2Mj\n" +
                "WYAwAAwbxd7e3pLQ3d7ednt7e9P3Dw8PAWDY+ADyHvj0Hgj57PPzMwAMGxdAXsckJGwhw1MRLsnp\n" +
                "8GQtAB4cHPR53sfHRwAYtpj5XK4VWkCk7O/vB4BhwwD4/PzcdA4eU+cEgGFzG/BYAE9OTnptr2SE\n" +
                "XivBbG9v97ofhWO18wPANbKnp6e+eKvpeMrxKABWqhs6YFhvTAjIyQijQLC7u9t7OIr1Sl5GUR1A\n" +
                "4/37+3uyfdqgHsdp2wOn48AaHnANTANuAQI+TQ6kwwEMx+UJBSAzXk0iKHd3d/37HIC5HHDVPd5a\n" +
                "AghICoHkZQzy2dlZd35+Pn1fmwTQBp4KMK+urnpovDgMlL69WSchAeASGjmYB0LAaFAJZQws0AFf\n" +
                "KtcCntosFM8mmAnTPiecR4YJAJfECGcKiRS7VirZQ8eAib9fX1/Tzw4PD3sQgUY5mpa6AHNra6sa\n" +
                "Ml9eXv7znJxvBeR5ANRseAwAbX+Vym8t9f0KgACgBNwacNg8zBcAmWX2CBwWUl77SYaO4S2Bi2uU\n" +
                "pA/qpCYNHkBtJmiZRNgHYigAldO29pUmWSsJoM3Bch1AWPQDyyASnjhG4TUDpbYIh9RJDRjX9KFN\n" +
                "g0uRSJzajVICgPpAJSnF79fjnqzM0jKo9j7pg1YBOwWd77+UbJRLYZTj2r2ISw9gDjjlZHbtVEKs\n" +
                "HUDbgXQw9W9ubqaaGp0mOHPXpz06WRs+1T4DZHeetHqgUs6WkmVqIXKW+jmA9J0WFa4tfNYr08dj\n" +
                "hOlRASwJrna/G19UIZmiTpSnqYUstUe+53NGte89QSlEtQAo2YXvQD6owfkJAO3asdUpWySfeVMm\n" +
                "9ffQOePoHtDvb/OF47ldIMoJU0+1Ded2wmFNgNVCqr+/FLAeADsJWQRATZLUXq1+Sri21x9TmLf5\n" +
                "9FDAjwag1jJz4Zew0RriUjuCefK9jpfaT9eSUymE2bZKYd0+AFq7zXk0LcVxDzxIugbX41z7cFmP\n" +
                "rBTF9qe8UK3/fkL2GirkT4aM8XiN1GK8n4TUOtALx7n6PqebRfbIPTD+eiXvnXqgclvsVayOWCpA\n" +
                "m9Ip/5Jp0sP352Gd52cDEx/jpYu1FK/D6WZKOUvrbxz8WmyuPiGMQfX3bz1Na4hpuZ4P2XgtHxK9\n" +
                "FKT6koFsf5RSFCsb/WVttiST1Ria5HSx1uIbrOVMrQD68N0KoDyFrjePEKzrCV7pljZEahJSAkow\n" +
                "+xQhlWKoPVt8TrvsCwWp8mNCtAZYsgmDW9qCrqW0llkh4Y/1WQuRUooWHSwFoJVptJ0K0MOWeCXE\n" +
                "epCWfGGW+srhJFwPsRZLjrPqG0LXFsDar8DstL8mi+SE79Ycap03AwSAmdk04W5RAFt/5mgB9JsL\n" +
                "wlYIQA9Iam3WyyrAMcvaqCZSrZIAsPnfZAwh64QtEYAeOK31zuKJ7OYDCb+t9f39rPI/AwoA59wv\n" +
                "Z+3x8bHb2dnpNjc3v7XH5xcXF9U2qENdFdoMCwCrdnx8nM0pI2QGgKMDaM+9vr7uyyr/96hVt3+m\n" +
                "7XbmQ1vdVQAAAABJRU5ErkJggg==";
        ChromeDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            webDriver.get("https://www.sonkwo.com/sign_up");
            WebElement formPanel = webDriver.findElement(By.cssSelector("#content-wrapper > div > div.sign-up-panel > div > div"));
            webDriver.executeScript("var newDiv = document.createElement('div');" +
                    "newDiv.style.cssText='margin:0 auto;border:1px solid #000;width:300px;height:100px;color:#F00;';" +//样式
                    "var codeNode = document.createTextNode('请输入验证码:');" +//内容
                    "var imgNode = document.createElement('img');" +//图片
                    "var buttonNode = document.createElement('button');" +//图片
                    "var buttonText = document.createTextNode('确定');" +//图片
                    "buttonNode.appendChild(buttonText);" +
                    "imgNode.src=arguments[1];" +//img
                    "var codeInput = document.createElement('input');" +
                    //添加事件
                    "buttonNode.onclick=function(){alert(codeInput.value);};" +
                    "imgNode.onclick=function(){imgNode.src=arguments[1];};" +
                    //添加元素
                    "newDiv.appendChild(imgNode);" +//div添加
                    "newDiv.appendChild(codeNode);" +//div添加
                    "newDiv.appendChild(buttonNode);" +//div添加
                    "newDiv.appendChild(codeInput);" +
                    "arguments[0].appendChild(newDiv);", formPanel, imgBase641, imgBase642);
            sleepSec(20);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
    }

    @Test
    public void clearData() {
        ChromeDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            webDriver.get("https://www.sonkwo.com/sign_up");
            webDriver.findElement(By.cssSelector("#phone_number")).sendKeys("13550995024");
            sleepSec(5);
            webDriver.findElement(By.cssSelector("#phone_number")).clear();
            sleepSec(5);
            webDriver.findElement(By.cssSelector("#phone_number")).sendKeys("18080487967");
            sleepSec(5);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.close();
        }
    }

    @Test
    public void assign() {
        ChromeDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            webDriver.get("https://www.sonkwo.com/sign_up");
            sleepSec(5);
            ((JavascriptExecutor) webDriver).executeScript("window.location.assign('https://dropmail.me/zh/');");
            sleepSec(5);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.close();
        }
    }

    public void sl(){
        ChromeDriver webDriver = new ChromeDriver(super.iniChromeOptions());
        try {
            webDriver.get("https://dropmail.me/zh/");
           webDriver.findElement(By.cssSelector("body > div.container > div:nth-child(6) > ul > li > div:nth-child(2)"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.close();
        }

    }
}
