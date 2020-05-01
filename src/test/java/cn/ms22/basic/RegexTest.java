package cn.ms22.basic;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
    @Test
    public void test() {
        String emails = "egsfljug@supere.ml\n" +
                "wsteyrqi@10mail.org\n" +
                "wsteafdb@10mail.org\n" +
                "wstedneh@10mail.org\n" +
                "wsteeavf@10mail.org\n" +
                "wstfarkg@10mail.org\n" +
                "wstfdkac@10mail.org\n" +
                "wstffsqw@10mail.org\n" +
                "wstfhzam@10mail.org\n" +
                "wstfkhpg@10mail.org\n" +
                "wstfnxob@10mail.org";
    }

    @Test
    public void test02(){
        Pattern pattern = Pattern.compile("^1000$|^([1-9]\\d{3,})(\\.\\d+)*$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(String.valueOf(1000));
        Assert.assertTrue(matcher.matches());
    }
}
