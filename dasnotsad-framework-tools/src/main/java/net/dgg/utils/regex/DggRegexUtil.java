package net.dgg.utils.regex;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.dgg.utils.constant.DggRegexConstant.*;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description: 正则工具类
 */
public class DggRegexUtil {

    private DggRegexUtil() {
        throw new UnsupportedOperationException("don't delusion instantiate me...");
    }

    /**
     * <p>
     * 是否为驼峰下划线混合命名
     * </p>
     *
     * @param param : 待判断字符串
     * @return
     */
    public static boolean isMixedMode(String param) {
        return isMatch(".*[A-Z]+.*", param) && isMatch(".*[/_]+.*", param);
    }

    /**
     * 判断字符串是否为大写命名
     *
     * @param param : 待判断字符串
     * @return {@code true} 如果是则返回
     */
    public static boolean isCapitalMode(String param) {
        return isMatch(UPPER_CAPITAL, param);
    }

    /**
     * @param phoneNumber 待验证手机号
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证手机号(简单版)
     */
    public static boolean isMobileSimple(String phoneNumber) {
        return isMatch(REGEX_MOBILE_SIMPLE, phoneNumber);
    }

    /**
     * @param phoneNumber 待验证手机号
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证手机号(精确版)
     */
    public static boolean isMobileExact(String phoneNumber) {
        return isMatch(REGEX_MOBILE_EXACT, phoneNumber);
    }

    /**
     * @param telNumber 待验证电话号
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证电话号码
     */
    public static boolean isTel(String telNumber) {
        return isMatch(REGEX_TEL, telNumber);
    }

    /**
     * @param cardNumber 待验证身份证号码
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证身份证号码15位
     */
    public static boolean isIDCard15(String cardNumber) {
        return isMatch(REGEX_ID_CARD15, cardNumber);
    }

    /**
     * @param cardNumber 待验证身份证号码
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证身份证号码18位
     */
    public static boolean isIDCard18(String cardNumber) {
        return isMatch(REGEX_ID_CARD18, cardNumber);
    }

    /**
     * @param emailAddress 待验证邮箱地址
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证邮箱
     */
    public static boolean isEmail(String emailAddress) {
        return isMatch(REGEX_EMAIL, emailAddress);
    }

    /**
     * @param url 待验证URL
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证URL
     */
    public static boolean isURL(String url) {
        return isMatch(REGEX_URL, url);
    }

    /**
     * @param zhText 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证中文
     */
    public static boolean isZh(String zhText) {
        return isMatch(REGEX_ZH, zhText);
    }

    /**
     * @param name 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证用户名
     * <p>取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是6-20位</p>
     */
    public static boolean isName(String name) {
        return isMatch(REGEX_USERNAME, name);
    }

    /**
     * @param dateStr 待验证日期字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证yyyy-MM-dd格式的日期校验，已考虑平闰年
     */
    public static boolean isDate(String dateStr) {
        return isMatch(REGEX_DATE, dateStr);
    }

    /**
     * @param ip 待验证IP地址
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 验证IP地址
     */
    public static boolean isIP(String ip) {
        return isMatch(REGEX_IP, ip);
    }

    /**
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     * @description: 判断是否匹配正则
     */
    public static boolean isMatch(String regex, CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

    /**
     * @param regex 正则表达式
     * @param input 要匹配的字符串
     * @return 正则匹配的部分
     * @description: 获取正则匹配的部分
     */
    public static List<String> getMatches(String regex, CharSequence input) {
        if (input == null) {
            return null;
        }
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    /**
     * @param input 要分组的字符串
     * @param regex 正则表达式
     * @return 正则匹配分组
     * @description: 获取正则匹配分组
     */
    public static String[] getSplits(String input, String regex) {
        if (input == null) {
            return null;
        }
        return input.split(regex);
    }

    /**
     * @param input       要替换的字符串
     * @param regex       正则表达式
     * @param replacement 代替者
     * @return 替换正则匹配的第一部分
     * @description: 替换正则匹配的第一部分
     */
    public static String getReplaceFirst(String input, String regex, String replacement) {
        if (input == null) {
            return null;
        }
        return Pattern.compile(regex).matcher(input).replaceFirst(replacement);
    }

    /**
     * @param input       要替换的字符串
     * @param regex       正则表达式
     * @param replacement 代替者
     * @return 替换所有正则匹配的部分
     * @description: 替换所有正则匹配的部分
     */
    public static String getReplaceAll(String input, String regex, String replacement) {
        if (input == null) {
            return null;
        }
        return Pattern.compile(regex).matcher(input).replaceAll(replacement);
    }

}
