package net.dgg.utils.string;


import net.dgg.utils.constant.CharConstant;
import net.dgg.utils.constant.StringPool;
import net.dgg.utils.core.DggArrayUtil;
import net.dgg.utils.core.DggCharsetUtil;
import net.dgg.utils.regex.DggRegexUtil;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import static net.dgg.utils.constant.StringPool.EMPTY;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description: String的工具类
 */
public class DggStringUtil {

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
     * @param str
     * @return
     * @description: 判断指定字符串是否为空
     */
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * @param str
     * @return
     * @description: 判断指定字符串是否包含文本内容(空格除外)
     */
    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param str
     * @return
     * @description: 判断指定字符串是否存在内容(包含空格)
     */
    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }


    /**
     * @param str
     * @return
     * @description: 去除指定字符串前后空格
     */
    public static String trimWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        int beginIndex = 0;
        int endIndex = str.length() - 1;
        while (beginIndex <= endIndex && Character.isWhitespace(str.charAt(beginIndex))) {
            beginIndex++;
        }
        while (endIndex > beginIndex && Character.isWhitespace(str.charAt(endIndex))) {
            endIndex--;
        }
        return str.substring(beginIndex, endIndex + 1);
    }

    /**
     * @param str
     * @return
     * @description: 去除指定字符串所有空格
     */
    public static String trimAllWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * @param str
     * @return
     * @description: 去除指定字符串前置空格
     */
    public static String trimLeadingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    /**
     * @param str
     * @return
     * @description: 去除指定字符串后置空格
     */
    public static String trimTrailingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * @param str
     * @param prefix
     * @return
     * @description: 是否匹配指定前缀字符串(忽略大小写)
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return (str != null && prefix != null && str.length() >= prefix.length() &&
                str.regionMatches(true, 0, prefix, 0, prefix.length()));
    }

    /**
     * @param str
     * @param suffix
     * @return
     * @description: 是否匹配指定后缀字符串(忽略大小写)
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return (str != null && suffix != null && str.length() >= suffix.length() &&
                str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length()));
    }

    /**
     * @param inString
     * @param oldPattern
     * @param newPattern
     * @return
     * @description: 字符替换
     */
    public static String replace(String inString, String oldPattern, String newPattern) {
        if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            return inString;
        }

        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);

        // 旧字符的位置
        int pos = 0;
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString.substring(pos, index));
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sb.append(inString.substring(pos));
        return sb.toString();
    }

    /**
     * @param str
     * @return
     * @description: 指定字符前后增加英文单引号
     */
    public static String quote(String str) {
        return (str != null ? "'" + str + "'" : null);
    }

    /**
     * @param str
     * @return
     * @description: 指定字符首字母大写
     */
    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * @param str
     * @return
     * @description: 指定字符首字母小写
     */
    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (!hasLength(str)) {
            return str;
        }

        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }

        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars, 0, chars.length);
    }

    /*--------------------------------------[ Add by yan.x 2020/8/4 begin ]------------------------------------*/

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj 对象
     * @return 字符串
     */
    public static String toString(Object obj) {
        return toString(obj, DggCharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 将对象转为字符串<br>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组 2、对象数组会调用Arrays.toString方法
     *
     * @param obj     对象
     * @param charset 字符集
     * @return 字符串
     */
    public static String toString(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return toEncodedString((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            return toEncodedString((Byte[]) obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return toString((ByteBuffer) obj, charset);
        } else if (DggArrayUtil.isArray(obj)) {
            return DggArrayUtil.toString(obj);
        }
        return obj.toString();
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String toEncodedString(final Byte[] data, final Charset charset) {
        if (data == null) {
            return null;
        }
        byte[] bytes = new byte[data.length];
        Byte dataByte;
        for (int i = 0; i < data.length; i++) {
            dataByte = data[i];
            bytes[i] = (null == dataByte) ? -1 : dataByte.byteValue();
        }
        return toEncodedString(bytes, charset);
    }

    public static String toEncodedString(final byte[] bytes, final Charset charset) {
        return new String(bytes, charset != null ? charset : Charset.defaultCharset());
    }


    public static Object getValues(String simplename, String value) {
        if ("int".equalsIgnoreCase(simplename) || "Integer".equalsIgnoreCase(simplename)) {
            return Integer.parseInt(value);
        }
        if ("double".equalsIgnoreCase(simplename)) {
            return Double.parseDouble(value);
        }
        if ("float".equalsIgnoreCase(simplename)) {
            return Float.parseFloat(value);
        }
        if ("string".equalsIgnoreCase(simplename)) {
            return value;
        }
        if ("boolean".equalsIgnoreCase(simplename)) {
            return Boolean.parseBoolean(value);
        }
        if ("long".equalsIgnoreCase(simplename)) {
            return Long.parseLong("90");
        }
        return null;
    }

    public static Object getValues(String simplename) {
        Random random = new Random();
        if ("int".equalsIgnoreCase(simplename) || "Integer".equalsIgnoreCase(simplename)) {
            return 123 + random.nextInt(20);
        }
        if ("double".equalsIgnoreCase(simplename)) {
            return 12.89 + random.nextInt(20);
        }
        if ("float".equalsIgnoreCase(simplename)) {
            return 11 + random.nextInt(20);
        }
        if ("string".equalsIgnoreCase(simplename)) {
            return "abcd" + random.nextInt(20);
        }
        if ("boolean".equalsIgnoreCase(simplename)) {
            return false;
        }
        if ("long".equalsIgnoreCase(simplename)) {
            return Long.parseLong("90" + random.nextInt(20));
        }
        return null;
    }

    /**
     * 向字符串中追加新的字符
     *
     * @param str
     * @param appendStr
     * @param otherwise
     * @return
     */
    public static String appendIfNotContain(String str, String appendStr, String otherwise) {
        if (StringUtils.isBlank(str) || StringUtils.isBlank(appendStr)) {
            return str;
        }
        if (str.contains(appendStr)) {
            return str.concat(otherwise);
        }
        return str.concat(appendStr);
    }

    /**
     * 字符串下划线转驼峰格式(首字母大写)
     *
     * @param source : 需要转换的字符串
     * @return
     */
    public static String underlineToCamel(final String source) {
        StringBuffer sbf = new StringBuffer();
        if (source.contains("_")) {
            String[] split = source.split("_");
            for (int i = 0, index = split.length; i < index; i++) {
                String upperTable = underlineToCamel(split[i]);
                sbf.append(upperTable);
            }
        } else {
            char[] ch = source.toCharArray();
            if (ch[0] >= 'a' && ch[0] <= 'z') {
                ch[0] = (char) (ch[0] - 32);
            }
            sbf.append(ch);
        }
        return sbf.toString();
    }

    /**
     * 字符串下划线转驼峰格式
     *
     * @param source        : 源字符串
     * @param isCapitalMode : 是否首字符大写(默认大写)
     * @return
     */
    public static String underlineToCamel(final String source, boolean isCapitalMode) {
        if (isEmpty(source)) {
            return "";
        }
        String tempStr = source;
        // 大写数字下划线组成转为小写 , 允许混合模式转为小写
        if (DggRegexUtil.isCapitalMode(source) || DggRegexUtil.isMixedMode(source)) {
            tempStr = source.toLowerCase();
        }
        StringBuilder result = new StringBuilder();
        String[] camels = tempStr.split("_");
        Arrays.stream(camels).filter(camel -> !isEmpty(camel)).forEach(camel -> {
            if (!isCapitalMode) {
                if (result.length() == 0) {
                    result.append(camel);
                } else {
                    result.append(capitalFirst(camel));
                }
            } else {
                // 首字母大写
                result.append(capitalFirst(camel));
            }
        });
        return result.toString();
    }

    /**
     * 驼峰式转下划线
     */
    public static String camelToUnderline(final String source) {
        StringBuilder result = new StringBuilder();
        if ((source != null) && (source.length() > 0)) {
            final String tags = "_";
            result.append(source.substring(0, 1).toLowerCase());
            for (int i = 1; i < source.length(); ++i) {
                String s = source.substring(i, i + 1);
                if (s.equals(s.toUpperCase())) {
                    if (s.equals(tags)) {
                        result.append(s);
                    } else {
                        result.append(tags);
                        result.append(s.toLowerCase());
                    }
                } else {
                    result.append(s);
                }
            }
        }
        return result.toString();
    }

    /**
     * 去掉字符串指定的前缀
     *
     * @param source : 源字符串
     * @param prefix : 前缀
     * @return
     */
    public static String removePrefix(final String source, String... prefix) {
        if (StringUtils.isEmpty(source)) {
            return StringPool.EMPTY;
        }
        if (null != prefix) {
            // 判断是否有匹配的前缀，然后截取前缀
            return Arrays.stream(prefix).filter(pf -> source.toLowerCase()
                    .matches(StringPool.HAT + pf.toLowerCase() + ".*"))
                    .findFirst().map(pf -> source.substring(pf.length())).orElse(source);
        }
        return source;
    }

    /**
     * 首字符转大写
     *
     * @param source : 待转换的字符串
     * @return : 转换后的字符串
     */
    public static String capitalFirst(final String source) {
        if (StringUtils.isNotEmpty(source)) {
            return source.substring(0, 1).toUpperCase() + source.substring(1);
        }
        return "";
    }

    /**
     * 拼接字符串第二个字符串第一个字母大写
     *
     * @param concatStr
     * @param str
     * @return
     */
    public static String concatCapitalize(String concatStr, final String str) {
        if (isEmpty(concatStr)) {
            concatStr = "";
        }
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        StringBuilder sb = new StringBuilder(strLen);
        sb.append(concatStr);
        sb.append(Character.toTitleCase(firstChar));
        sb.append(str.substring(1));
        return sb.toString();
    }


    /**
     * 包装指定字符串<br>
     * 当前缀和后缀一致时使用此方法
     *
     * @param str             被包装的字符串
     * @param prefixAndSuffix 前缀和后缀
     * @return 包装后的字符串
     */
    public static String wrap(final String str, final String prefixAndSuffix) {
        return wrap(str, prefixAndSuffix, prefixAndSuffix);
    }

    /**
     * 包装指定字符串
     *
     * @param str    被包装的字符串
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 包装后的字符串
     */
    public static String wrap(final String str, final String prefix, final String suffix) {
        return defaultString(str).concat(defaultString(prefix)).concat(defaultString(suffix));
    }

    /**
     * <p>编码字符串</p>
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(final CharSequence str, final Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    public static String defaultString(final String str) {
        return defaultString(str, EMPTY);
    }

    public static String defaultString(final String str, final String defaultStr) {
        return str == null ? defaultStr : str;
    }


    /**
     * 格式化字符串<br>
     * 此方法只是简单将占位符 {} 按照顺序替换为参数<br>
     * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br>
     * 例：<br>
     * 通常使用：format("this is {} for {}", "a", "b") =》 this is a for b<br>
     * 转义{}： 	format("this is \\{} for {}", "a", "b") =》 this is \{} for a<br>
     * 转义\：		format("this is \\\\{} for {}", "a", "b") =》 this is \a for b<br>
     *
     * @param strPattern 字符串模板
     * @param argArray   参数列表
     * @return 结果
     */
    public static String format(final String strPattern, final Object... argArray) {
        if (StringUtils.isBlank(strPattern) || DggArrayUtil.isEmpty(argArray)) {
            return strPattern;
        }
        final int strPatternLength = strPattern.length();

        //初始化定义好的长度以获得更好的性能
        StringBuilder sbuf = new StringBuilder(strPatternLength + 50);

        int handledPosition = 0;//记录已经处理到的位置
        int delimIndex;//占位符所在位置
        for (int argIndex = 0; argIndex < argArray.length; argIndex++) {
            delimIndex = strPattern.indexOf(StringPool.EMPTY_JSON, handledPosition);
            if (delimIndex == -1) {//剩余部分无占位符
                if (handledPosition == 0) { //不带占位符的模板直接返回
                    return strPattern;
                } else { //字符串模板剩余部分不再包含占位符，加入剩余部分后返回结果
                    sbuf.append(strPattern, handledPosition, strPatternLength);
                    return sbuf.toString();
                }
            } else {
                if (delimIndex > 0 && strPattern.charAt(delimIndex - 1) == CharConstant.BACKSLASH) {//转义符
                    if (delimIndex > 1 && strPattern.charAt(delimIndex - 2) == CharConstant.BACKSLASH) {//双转义符
                        //转义符之前还有一个转义符，占位符依旧有效
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(toString(argArray[argIndex]));
                        handledPosition = delimIndex + 2;
                    } else {
                        //占位符被转义
                        argIndex--;
                        sbuf.append(strPattern, handledPosition, delimIndex - 1);
                        sbuf.append(CharConstant.DELIM_START);
                        handledPosition = delimIndex + 1;
                    }
                } else {//正常占位符
                    sbuf.append(strPattern, handledPosition, delimIndex);
                    sbuf.append(toString(argArray[argIndex]));
                    handledPosition = delimIndex + 2;
                }
            }
        }
        // append the characters following the last {} pair.
        //加入最后一个占位符后所有的字符
        sbuf.append(strPattern, handledPosition, strPattern.length());

        return sbuf.toString();
    }

    /**
     * 比较两个字符串是否相等。
     *
     * @param str1       要比较的字符串1
     * @param str2       要比较的字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     * @since 3.2.0
     */
    public static boolean equals(final CharSequence str1, final CharSequence str2, boolean ignoreCase) {
        if (ignoreCase) {
            return StringUtils.equalsIgnoreCase(str1, str2);
        } else {
            return StringUtils.equals(str1, str2);
        }
    }
    /*--------------------------------------[ Add by yan.x 2020/8/4  end  ]------------------------------------*/
}
