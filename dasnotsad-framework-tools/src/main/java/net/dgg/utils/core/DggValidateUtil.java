package net.dgg.utils.core;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证帮助类
 *
 * @since 1.0
 */
public class DggValidateUtil {

    /**
     * 手机号码正则表达式
     */
    public static final String EXP_PHONE = "1\\d{10}";

    /**
     * 18位身份证号码正则表达式
     */
    public static final String EXP_18_IDENTITYNO = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

    /**
     * 15位身份证号码正则表达式
     */
    public static final String EXP_15_IDENTITYNO = "^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$";

    /**
     * IPV4正则表达式
     */
    public static final String EXP_IPV4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";

    /**
     * 邮件正则表达式
     */
    public static final String EXP_EMAIL = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * @param exceptionClazz 异常类
     * @param message        提示信息
     * @return
     */
    private static <T extends RuntimeException> T constructException(Class<T> exceptionClazz, String message) {
        T exception = null;

        Constructor<T> constructor = null;
        try {
            constructor = exceptionClazz.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            exception = (T) constructor.newInstance(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return exception;
    }

    /**
     * 处理验证结果的私有方法，减少重复代码量
     *
     * @param result         结果本身
     * @param exceptionClass 需要抛出的异常类实例
     * @param msg            需要返回的消息
     * @param <T>            需要抛出的异常类类型
     * @return 结果
     */
    private static <T extends RuntimeException> boolean getResult(boolean result, Class<T> exceptionClass, String msg) {
        // 如果结果为false，并且需要抛出异常
        if (!result && exceptionClass != null) {
            throw constructException(exceptionClass, msg);
        }

        return result;
    }

    /**
     * 表达式如果不为真，则抛出指定的异常
     *
     * @param result
     * @param exceptionClass
     * @param msg
     * @param <T>
     * @return
     */
    public static <T extends RuntimeException> boolean isTrue(boolean result, Class<T> exceptionClass, String msg) {

        return getResult(result, exceptionClass, msg == null ? "表达式不为真" : msg);
    }

    /**
     * 判断字符串是否符合正则表达式，正则标志位使用0
     *
     * @param data           被校验的对象
     * @param expression     正则表达式
     * @param exceptionClass 抛出的异常类型，为空时则不抛出异常
     * @param msg            异常时的报错信息，如果为null，则为默认信息
     * @param <T>            异常类型必须是RuntimeException的派生类
     * @return 是否匹配
     */
    public static <T extends RuntimeException> boolean matchExpression(String data, String expression,
                                                                       Class<T> exceptionClass, String msg) {
        return matchExpression(data, expression, 0, exceptionClass, msg);
    }

    /**
     * 判断字符串是否符合正则表达式
     *
     * @param data           被校验的对象
     * @param expression     正则表达式
     * @param expFlag        正则表达式验证标志位，java.util.regex.Pattern中存在指定常量，默认使用0
     * @param exceptionClass 抛出的异常类型，为空时则不抛出异常
     * @param msg            异常时的报错信息，如果为null，则为默认信息
     * @param <T>            异常类型必须是RuntimeException的派生类
     * @return 是否匹配
     */
    public static <T extends RuntimeException> boolean matchExpression(String data, String expression, int expFlag,
                                                                       Class<T> exceptionClass, String msg) {

        boolean result = false;

        result = DggValidateUtil.strNotEmpty(data, exceptionClass, "被校验字符串不可为空");
        result = DggValidateUtil.strNotEmpty(expression, exceptionClass, "表达式不可为空");

        if (result) {

            // 编译正则表达式
            Pattern pattern = Pattern.compile(expression, expFlag);
            Matcher matcher = pattern.matcher(data);
            result = matcher.matches();
        }

        return getResult(result, exceptionClass, msg == null ? "字符串不符合目标正则表达式规则" : msg);
    }

    /**
     * 非空校验
     *
     * @param data           被校验的对象
     * @param exceptionClass 抛出的异常类型，为空时则不抛出异常
     * @param msg            异常时的报错信息，如果为null，则为默认信息
     * @param <T>            异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean notNull(Object data, Class<T> exceptionClass, String msg) {
        return getResult(data != null, exceptionClass, msg == null ? "内容不可为null" : msg);
    }

    /**
     * 字符串不可为空的校验
     *
     * @param data                           被校验的对象
     * @param exceptionClass                 抛出的异常类型，为空时则不抛出异常
     * @param msg                            异常时的报错信息，如果为null，则为默认信息
     * @param <T>异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean strNotEmpty(String data, Class<T> exceptionClass, String msg) {
        boolean result = strNotEmpty(data);
        return getResult(result, exceptionClass, msg == null ? "内容不可为空" : msg);
    }

    /**
     * 字符串不可为空的校验
     *
     * @param data
     * @return
     */
    public static boolean strNotEmpty(String data) {
        boolean flag = false;
        if (data != null && !"".equals(data)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 字符串不可为空的校验，空字符也会被判定为空字符串，但不会改变入参字符串本身 true使用spring的StringUtils.trimWhitespace
     *
     * @param data                           被校验的对象
     * @param exceptionClass                 抛出的异常类型，为空时则不抛出异常
     * @param msg                            异常时的报错信息，如果为null，则为默认信息
     * @param <T>异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean strNotEmptyWithTrim(String data, Class<T> exceptionClass,
                                                                           String msg) {
        boolean result = strNotEmptyWithTrim(data);
        return getResult(result, exceptionClass, msg == null ? "内容不可为空" : msg);
    }

    /**
     * 字符串不可为空的校验，空字符也会被判定为空字符串，但不会改变入参字符串本身
     *
     * @param data
     * @return
     */
    public static boolean strNotEmptyWithTrim(String data) {
        boolean result = false;
        if (data != null) {
            String trimData = data.trim();
            if (!"".equals(trimData)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 校验整数是否大于某个值
     *
     * @param data                           被校验的数据
     * @param destNum                        被比较的目标值
     * @param containDest                    是否包含等于目标值
     * @param exceptionClass                 抛出的异常类型，为空时则不抛出异常
     * @param msg                            异常时的报错信息，如果为null，则为默认信息
     * @param <T>异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean intGreaterThan(int data, int destNum, boolean containDest,
                                                                      Class<T> exceptionClass, String msg) {
        return longGreaterThan(data, destNum, containDest, exceptionClass, msg);
    }

    /**
     * 校验整数是否小于某个值
     *
     * @param data                           被校验的数据
     * @param destNum                        被比较的目标值
     * @param containDest                    是否包含等于目标值
     * @param exceptionClass                 抛出的异常类型，为空时则不抛出异常
     * @param msg                            异常时的报错信息，如果为null，则为默认信息
     * @param <T>异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean intLessThan(int data, int destNum, boolean containDest,
                                                                   Class<T> exceptionClass, String msg) {
        return longLessThan(data, destNum, containDest, exceptionClass, msg);
    }

    /**
     * 校验整数是否小于某个值
     *
     * @param data                           被校验的数据
     * @param destNum                        被比较的目标值
     * @param containDest                    是否包含等于目标值
     * @param exceptionClass                 抛出的异常类型，为空时则不抛出异常
     * @param msg                            异常时的报错信息，如果为null，则为默认信息
     * @param <T>异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean longLessThan(long data, long destNum, boolean containDest,
                                                                    Class<T> exceptionClass, String msg) {

        boolean result = false;
        String remind;
        if (containDest) {
            remind = "值应小于等于";
            result = data <= destNum;
        } else {
            remind = "值应小于";
            result = data < destNum;
        }
        return getResult(result, exceptionClass, msg == null ? remind + destNum : msg);
    }

    /**
     * 校验整数是否大于某个值
     *
     * @param data                           被校验的数据
     * @param destNum                        被比较的目标值
     * @param containDest                    是否包含等于目标值
     * @param exceptionClass                 抛出的异常类型，为空时则不抛出异常
     * @param msg                            异常时的报错信息，如果为null，则为默认信息
     * @param <T>异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean longGreaterThan(long data, long destNum, boolean containDest,
                                                                       Class<T> exceptionClass, String msg) {

        boolean result = false;
        String remind;
        if (containDest) {
            remind = "值应大于等于";
            result = data >= destNum;
        } else {
            remind = "值应大于";
            result = data > destNum;
        }
        return getResult(result, exceptionClass, msg == null ? remind + destNum : msg);
    }

    /**
     * map不可为空的校验
     *
     * @param data                           被校验的对象
     * @param exceptionClass                 抛出的异常类型，为空时则不抛出异常
     * @param msg                            异常时的报错信息，如果为null，则为默认信息
     * @param <T>异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean mapNotEmpty(Map<String, ?> data, Class<T> exceptionClass,
                                                                   String msg) {
        boolean result = false;
        if (data != null && data.size() > 0) {
            result = true;
        }

        return getResult(result, exceptionClass, msg == null ? "字典不可为空" : msg);
    }

    /**
     * collection不可为空的校验
     *
     * @param data                           被校验的对象
     * @param exceptionClass                 抛出的异常类型，为空时则不抛出异常
     * @param msg                            异常时的报错信息，如果为null，则为默认信息
     * @param <T>异常类型必须是RuntimeException的派生类
     */
    public static <T extends RuntimeException> boolean collectionNotEmpty(Collection<T> data, Class<T> exceptionClass,
                                                                          String msg) {
        boolean result = collectionNotEmpty(data);
        return getResult(result, exceptionClass, msg == null ? "集合不可为空" : msg);
    }

    /**
     * collection不可为空的校验
     *
     * @param data
     * @return
     */
    public static boolean collectionNotEmpty(Collection<?> data) {
        boolean result = false;
        if (data != null && data.size() > 0) {
            result = true;
        }
        return result;
    }

    /**
     * @param regex 正则表达式字符串
     * @param text  要匹配的字符串
     * @return 如果text符合 regex的正则表达式格式,返回true， 否则返回 false
     */
    public static boolean isTrue(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static boolean isEmail(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return isTrue(regex, email);
    }

}
