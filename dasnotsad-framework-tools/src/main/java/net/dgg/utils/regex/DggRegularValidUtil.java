package net.dgg.utils.regex;

/**
 * @Description 正则验证工具类
 * @Version 1.0.0
 * @Author Created by yan.x on 2019-04-29 .
 * @Copyright © dgg group.All Rights Reserved.
 **/
public final class DggRegularValidUtil {

    /*--------------------------[ 常量 ]--------------------------*/
    /**
     * base64
     */
    public static final String BASE64 = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
    /**
     * 手机号码
     */
    public static final String MOBILE = "^(13|14|15|16|17|18|19)[0-9]{9}$";
    /**
     * 手机号码
     */
    public static final String CODE_AND_MOBILE = "^\\+[0-9]{2}\\-(13|14|15|16|17|18|19)[0-9]{9}$";
    /**
     * 电话号码的函数(包括验证国内区号;国际区号;分机号)
     */
    public static final String PHONE_NUMBER = "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{1,}))?$";
    /**
     * 整数
     */
    public static final String INTEGER = "^-?[1-9]\\d*$";
    /**
     * 整数
     */
    public static final String INTEGER_1 = "^[-]?\\d+$";
    /**
     * 正整数
     */
    public static final String POSITIVE_INTEGER = "^[1-9]\\d*$";
    /**
     * 正整数
     */
    public static final String POSITIVE_INTEGER_1 = "^\\d+$";
    /**
     * 负整数
     */
    public static final String NEGATIVE_INTEGER = "^-[1-9]\\d*$";
    /**
     * 数字
     */
    public static final String NUMBER = "^([+-]?)\\d*\\.?\\d+$";
    /**
     * 空白合同总金额验证
     */
    public static final String BLANK_ORDER_AMOUNT = "^(([3-9]\\d{2,2})|([1-9]\\d{3,7}))(.00|.0|)$";
    /**
     * 正数（正整数 + 0）
     */
    public static final String POSITIVE_NUMBER = "^[1-9]\\d*|0$";
    /**
     * 负数（负整数 + 0）
     */
    public static final String NEGATIVE_NUMBER = "^-[1-9]\\d*|0$";
    /**
     * 浮点数
     */
    public static final String FLOAT = "^([+-]?)\\d*\\.\\d+$";
    /**
     * 浮点数
     */
    public static final String FLOAT_1 = "^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$";
    /**
     * 浮点数
     */
    public static final String FLOAT_2 = "^[-]?\\d+(\\.\\d+)?$";
    /**
     * 正浮点数
     */
    public static final String POSITIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$";
    /**
     * 判断字符串是否为正浮点数
     */
    public static final String POSITIVE_FLOAT_1 = "^\\d+(\\.\\d+)?$";
    /**
     * 负浮点数
     */
    public static final String NEGATIVE_FLOAT = "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
    /**
     * 非负浮点数（正浮点数 + 0）
     */
    public static final String NOT_NEGATIVE_FLOAT = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$";
    /**
     * 非正浮点数（负浮点数 + 0）
     */
    public static final String NOT_POSITIVE_FLOAT = "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$";
    /**
     * 邮件
     */
    public static final String EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
    /**
     * 颜色
     */
    public static final String COLOR_NUM = "^[a-fA-F0-9]{6}$";
    /**
     * url
     */
    public static final String URL = "^http[s]?=\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$";
    /**
     * 仅中文
     */
    public static final String CHINESE = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
    /**
     * 仅ACSII字符
     */
    public static final String ASCII = "^[\\x00-\\xFF]+$";
    /**
     * 邮编
     */
    public static final String ZIPCODE = "^\\d{6}$";
    /**
     * ip地址
     */
    public static final String IP4 = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
    /**
     * 非空
     */
    public static final String NOTEMPTY = "^\\S+$";
    /**
     * 图片
     */
    public static final String PICTURE = "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$";
    /**
     * 压缩文件
     */
    static final String RAR = "(.*)\\.(rar|zip|7zip|tgz)$";
    /**
     * 日期
     */
    public static final String DATE = "^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$";
    /**
     * QQ号码
     */
    public static final String QQ = "^[1-9]*[1-9][0-9]*$";
    /**
     * 用来用户注册。匹配由数字、26个英文字母或者下划线组成的字符串
     */
    public static final String USERNAME = "^\\w+$";
    /**
     * 字母
     */
    public static final String LETTER = "^[A-Za-z]+$";
    /**
     * 字母(包含空格)
     */
    public static final String LETTER_AND_SPACE = "^[A-Za-z ]+$";
    /**
     * 首字母大写
     */
    public static final String UPPER_CAPITAL = "^[0-9A-Z/_]+$";
    /**
     * 大写字母
     */
    public static final String UPPER_LETTER = "^[A-Z]+$";
    /**
     * 小写字母
     */
    public static final String LOW_LETTER = "^[a-z]+$";
    /**
     * 身份证
     */
    public static final String IDCARD = "^[1-9]([0-9]{14}|[0-9]{17})$";
    /**
     * 判断车辆Vin码
     */
    public static final String CAR_VIN = "^[1234567890WERTYUPASDFGHJKLZXCVBNM]{13}[0-9]{4}$";

    /*--------------------------[ 验证方法 ]--------------------------*/

    /**
     * 验证是否是base64字符串
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isBase64(String param) {
        return matches(BASE64, param);
    }


    /**
     * 金额验证(7为正浮点数,最小为1)
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isSpecialCharacter(String param) {
        return matches(".*[<'\">].*", param);
    }

    /**
     * 金额验证(7为正浮点数,最小为1)
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isMoneyByMaxSize7(String param) {
        return matches("^[1-9][0-9]{0,6}(\\.[0-9]{2}|\\.[0-9]|)$", param);
    }

    /**
     * 合同金额验证(8为正浮点数,最小为1)
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isMoneyByMaxSize8(String param) {
        return matches("^[1-9][0-9]{0,7}(\\.[0-9]{2}|\\.[0-9]|)$", param);
    }

    /**
     * 手机号验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isMobile(String param) {
        return matches(MOBILE, param);
    }

    /**
     * 手机号验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isCodeAndMobile(String param) {
        return matches(CODE_AND_MOBILE, param);
    }

    /**
     * 电话号码的函数(包括验证国内区号;国际区号;分机号)验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isPhoneNumber(String param) {
        return matches(PHONE_NUMBER, param);
    }

    /**
     * 整数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isInteger(String param) {
        return matches(INTEGER, param);
    }

    /**
     * 整数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isInteger1(String param) {
        return matches(INTEGER_1, param);
    }

    /**
     * 正整数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isPositiveInteger(String param) {
        return matches(POSITIVE_INTEGER, param);
    }

    /**
     * 正整数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isPositiveInteger1(String param) {
        return matches(POSITIVE_INTEGER_1, param);
    }

    /**
     * 负整数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isNegativeInteger(String param) {
        return matches(NEGATIVE_INTEGER, param);
    }

    /**
     * 数字验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isNumber(String param) {
        return matches(NUMBER, param);
    }

    /**
     * 正数（正整数 + 0）验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isPositiveNumber(String param) {
        return matches(POSITIVE_NUMBER, param);
    }

    /**
     * 负数（负整数 + 0）验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isNegativeNumber(String param) {
        return matches(NEGATIVE_NUMBER, param);
    }

    /**
     * 浮点数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isFloat(String param) {
        return matches(FLOAT, param);
    }

    /**
     * 浮点数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isFloat1(String param) {
        return matches(FLOAT_1, param);
    }

    /**
     * 浮点数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isFloat2(String param) {
        return matches(FLOAT_2, param);
    }

    /**
     * 正浮点数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isPositiveFloat(String param) {
        return matches(POSITIVE_FLOAT, param);
    }

    /**
     * 正浮点数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isPositiveFloat1(String param) {
        return matches(POSITIVE_FLOAT_1, param);
    }

    /**
     * 负浮点数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isNegativeFloat(String param) {
        return matches(NEGATIVE_FLOAT, param);
    }

    /**
     * 非负浮点数（正浮点数 + 0）验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isNotNegativeFloat(String param) {
        return matches(NOT_NEGATIVE_FLOAT, param);
    }

    /**
     * 非正浮点数（负浮点数 + 0）验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isNotPositiveFloat(String param) {
        return matches(NOT_POSITIVE_FLOAT, param);
    }

    /**
     * 邮箱格式验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isEmail(String param) {
        return matches(EMAIL, param);
    }

    /**
     * 颜色验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isColorNum(String param) {
        return matches(COLOR_NUM, param);
    }

    /**
     * url验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isUrl(String param) {
        return matches(URL, param);
    }

    /**
     * 中文验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isChinese(String param) {
        return matches(CHINESE, param);
    }

    /**
     * ascii码验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isAscii(String param) {
        return matches(ASCII, param);
    }

    /**
     * 邮编验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isZipcode(String param) {
        return matches(ZIPCODE, param);
    }

    /**
     * IP地址验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isIP4(String param) {
        return matches(IP4, param);
    }

    /**
     * 非空验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isNotEmpty(String param) {
        return matches(NOTEMPTY, param);
    }

    /**
     * 图片验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isPicture(String param) {
        return matches(PICTURE, param);
    }

    /**
     * 压缩文件验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isRar(String param) {
        return matches(RAR, param);
    }

    /**
     * 日期格式验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isDate(String param) {
        return matches(DATE, param);
    }

    /**
     * qq号码验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isQQ(String param) {
        return matches(QQ, param);
    }

    /**
     * 用来用户注册。匹配由数字、26个英文字母或者下划线组成的字符串验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isUserName(String param) {
        return matches(USERNAME, param);
    }

    /**
     * 字母验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isLetter(String param) {
        return matches(LETTER, param);
    }

    /**
     * 字母验证(包含空格)
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isLetterAndSpace(String param) {
        return matches(LETTER_AND_SPACE, param);
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
        return matches(".*[A-Z]+.*", param) && matches(".*[/_]+.*", param);
    }

    /**
     * 小写字母验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isLowLetter(String param) {
        return matches(LOW_LETTER, param);
    }

    /**
     * 判断字符串是否为大写命名
     *
     * @param param : 待判断字符串
     * @return {@code true} 如果是则返回
     */
    public static boolean isCapitalMode(String param) {
        return matches(UPPER_CAPITAL, param);
    }

    /**
     * 大写字母
     */
    /**
     * 正整数验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isUpperLetter(String param) {
        return matches(UPPER_LETTER, param);
    }

    /**
     * 身份证号码验证
     *
     * @param param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isIDCard(String param) {
        return matches(IDCARD, param);
    }

    /**
     * 车辆Vin码验证
     *
     * @param : 待验证的字符串
     * @return {@code true} 如果正确则返回.
     */
    public static boolean isCarVin(String param) {
        return matches(CAR_VIN, param);
    }


    /**
     * 正则表达式匹配
     *
     * @param regex : 正则表达式字符串
     * @param param : 要匹配的字符串
     * @return {@code true} 如果匹配则返回
     */
    public static boolean matches(String regex, String param) {
        if (org.apache.commons.lang3.StringUtils.isBlank(param)) return false;
        return param.matches(regex);
    }
}
