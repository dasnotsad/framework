package utils.regex;

import net.dgg.utils.regex.DggRegexUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description:
 */
public class DggRegexUtilTest {

    @Test
    public void isMobileSimpleTest() {
        String phoneNumber = "13100002222";
        boolean result = DggRegexUtil.isMobileSimple(phoneNumber);
        Assert.assertTrue("简单手机号验证失败!", result);
    }

    @Test
    public void isMobileExactTest() {
        String phoneNumber = "16600002222";
        boolean result = DggRegexUtil.isMobileExact(phoneNumber);
        Assert.assertTrue("精确手机号验证失败!", result);
    }

    @Test
    public void isTelTest() {
        String telNumber = "010-87654321";
        boolean result = DggRegexUtil.isTel(telNumber);
        Assert.assertTrue("电话号验证失败!", result);
    }

    @Test
    public void isIDCard15Test() {
//        String cardNumber = "320311770706001";
        String cardNumber = "320311000101601";
        boolean result = DggRegexUtil.isIDCard15(cardNumber);
        Assert.assertTrue("15位身份证号验证失败!", result);
    }

    @Test
    public void isIDCard18Test() {
        String cardNumber = "130503206701000012";
        boolean result = DggRegexUtil.isIDCard18(cardNumber);
        //Assert.assertTrue("18位身份证号验证失败!", result);
    }

    @Test
    public void isEmailTest() {
        String emailAddress = "abc@163.com";
        boolean result = DggRegexUtil.isEmail(emailAddress);
        Assert.assertTrue("邮箱验证失败!", result);
    }

    @Test
    public void isURLTest() {
        String url = "http://www.baidu.com/";
        boolean result = DggRegexUtil.isURL(url);
        Assert.assertTrue("url验证失败!", result);
    }

    @Test
    public void isZhTest() {
        String text = "我是中国人";
        boolean result = DggRegexUtil.isZh(text);
        Assert.assertTrue("中文验证失败!", result);
    }

    @Test
    public void isNameTest() {
        String name = "shitiankexin";
        boolean result = DggRegexUtil.isName(name);
        Assert.assertTrue("名字验证失败!", result);
    }

    @Test
    public void isDateTest() {
        String date = "2019-06-24";
        boolean result = DggRegexUtil.isDate(date);
        Assert.assertTrue("日期验证失败!", result);
    }

    @Test
    public void isIPTest() {
        String ip = "1.1.1.255";
        boolean result = DggRegexUtil.isIP(ip);
        Assert.assertTrue("IP地址验证失败!", result);
    }

}
