package net.dgg.utils.exception;


import net.dgg.utils.core.DggArrayUtil;
import net.dgg.utils.string.DggStringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

/**
 * @Description: 断言
 * @Author Created by yan.x on 2019-08-20 .
 **/
public final class DggAsserts {

    /**
     * 构造异常信息
     *
     * @param errorMsg
     */
    public static boolean build(String errorMsg) {
        return build(DggUtilException.class, errorMsg);
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@link DggUtilException} 异常<br>
     * <p>
     * <pre class="code">
     * DggAsserts.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws DggUtilException if expression is {@code false}
     */
    public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (false == expression) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
    }

    /**
     * 断言是否为真，如果为 {@code false} 抛出 {@link DggUtilException} 异常<br>
     * <p>
     * <pre class="code">
     * DggAsserts.isTrue(i &gt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression 布尔值
     * @throws DggUtilException if expression is {@code false}
     */
    public static void isTrue(boolean expression) throws DggUtilException {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@link DggUtilException} 异常<br>
     * <p>
     * <pre class="code">
     * DggAsserts.isFalse(i &lt; 0, "The value must be greater than zero");
     * </pre>
     *
     * @param expression       布尔值
     * @param errorMsgTemplate 错误抛出异常附带的消息模板，变量用{}代替
     * @param params           参数列表
     * @throws DggUtilException if expression is {@code false}
     */
    public static void isFalse(boolean expression, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (true == expression) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
    }

    /**
     * 断言是否为假，如果为 {@code true} 抛出 {@link DggUtilException} 异常<br>
     * <p>
     * <pre class="code">
     * DggAsserts.isFalse(i &lt; 0);
     * </pre>
     *
     * @param expression 布尔值
     * @throws DggUtilException if expression is {@code false}
     */
    public static void isFalse(boolean expression) throws DggUtilException {
        isFalse(expression, "[Assertion failed] - this expression must be false");
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link DggUtilException} 异常
     * <p>
     * <pre class="code">
     * DggAsserts.isNull(value, "The value must be null");
     * </pre>
     *
     * @param object           被检查的对象
     * @param errorMsgTemplate 消息模板，变量使用{}表示
     * @param params           参数列表
     * @throws DggUtilException if the object is not {@code null}
     */
    public static void isNull(Object object, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (object != null) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
    }

    /**
     * 断言对象是否为{@code null} ，如果不为{@code null} 抛出{@link DggUtilException} 异常
     * <p>
     * <pre class="code">
     * DggAsserts.isNull(value);
     * </pre>
     *
     * @param object 被检查对象
     * @throws DggUtilException if the object is not {@code null}
     */
    public static void isNull(Object object) throws DggUtilException {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    //----------------------------------------------------------------------------------------------------------- Check not null

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link DggUtilException} 异常 Assert that an object is not {@code null} .
     * <p>
     * <pre class="code">
     * DggAsserts.isNotNull(clazz, "The class must not be null");
     * </pre>
     *
     * @param <T>              被检查对象泛型类型
     * @param object           被检查对象
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 被检查后的对象
     * @throws DggUtilException if the object is {@code null}
     */
    public static <T> T isNotNull(T object, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (object == null) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return object;
    }

    /**
     * 断言对象是否不为{@code null} ，如果为{@code null} 抛出{@link DggUtilException} 异常
     * <p>
     * <pre class="code">
     * DggAsserts.isNotNull(clazz);
     * </pre>
     *
     * @param <T>    被检查对象类型
     * @param object 被检查对象
     * @return 非空对象
     * @throws DggUtilException if the object is {@code null}
     */
    public static <T> T isNotNull(T object) throws DggUtilException {
        return isNotNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    //----------------------------------------------------------------------------------------------------------- Check empty

    /**
     * 检查给定字符串是否不为空，不为空抛出 {@link DggUtilException}
     * <p>
     * <pre class="code">
     * DggAsserts.isEmpty(name, "Name must not be empty");
     * </pre>
     *
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws DggUtilException 被检查字符串为空
     * @see MessageFormat#isEmpty(CharSequence)
     */
    public static String isEmpty(String text, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (DggStringUtil.isNotEmpty(text)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return text;
    }

    /**
     * 检查给定字符串是否不为空，不为空抛出 {@link DggUtilException}
     * <p>
     * <pre class="code">
     * DggAsserts.isEmpty(name);
     * </pre>
     *
     * @param text 被检查字符串
     * @return 被检查的字符串
     * @throws DggUtilException 被检查字符串为空
     * @see MessageFormat#isEmpty(CharSequence)
     */
    public static String isEmpty(String text) throws DggUtilException {
        return isEmpty(text, "[Assertion failed] - The string argument is empty or empty");
    }

    /**
     * 检查给定字符串是否不为空白（null、空串或只包含空白符），不为空抛出 {@link DggUtilException}
     * <p>
     * <pre class="code">
     * DggAsserts.isBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws DggUtilException 被检查字符串为空白
     * @see MessageFormat#isBlank(CharSequence)
     */
    public static String isBlank(String text, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (StringUtils.isNotBlank(text)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return text;
    }

    /**
     * 检查给定字符串是否不为空白（null、空串或只包含空白符），不为空抛出 {@link DggUtilException}
     * <p>
     * <pre class="code">
     * DggAsserts.isNotBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param text 被检查字符串
     * @return 非空字符串
     * @throws DggUtilException 被检查字符串为空白
     * @see MessageFormat#isBlank(CharSequence)
     */
    public static String isBlank(String text) throws DggUtilException {
        return isBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * 断言给定字符串是否被另一个字符串包含（既是否为子串）
     * <p>
     * <pre class="code">
     * DggAsserts.isContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch     被搜索的字符串
     * @param substring        被检查的子串
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的子串
     * @throws DggUtilException 非子串抛出异常
     */
    public static String isContain(String textToSearch, String substring, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (DggStringUtil.isEmpty(textToSearch) || DggStringUtil.isEmpty(substring) || !textToSearch.contains(substring)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return substring;
    }

    /**
     * 断言给定字符串是否被另一个字符串包含（既是否为子串）
     * <p>
     * <pre class="code">
     * DggAsserts.isContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch 被搜索的字符串
     * @param substring    被检查的子串
     * @return 被检查的子串
     * @throws DggUtilException 非子串抛出异常
     */
    public static String isContain(String textToSearch, String substring) throws DggUtilException {
        return isContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [{}]", substring);
    }

    /**
     * 断言给定数组是否不包含元素，数组必须为 {@code null} 或没有元素
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param objects  被检查的数组
     * @param errorMsg 异常信息
     * @return 被检查的数组
     * @throws DggUtilException if the object array is not {@code null} and contains at least one elements
     */
    public static Object[] isEmpty(String errorMsg, Object... objects) throws DggUtilException {
        isNull(objects, errorMsg);
        for (Object object : objects) {
            isNull(object, errorMsg);
            if (object instanceof String) {
                String trim = object.toString().trim();
                isTrue("".equals(trim) || "null".equals(trim), errorMsg);
            }
        }
        return objects;
    }

    /**
     * 断言给定数组是否不包含元素，数组必须为 {@code null} 或没有元素
     * <p>
     * <pre class="code">
     * DggAsserts.isEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param array            被检查的数组
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的数组
     * @throws DggUtilException if the object array is not {@code null} and contains at least one elements
     */
    public static Object[] isEmpty(Object[] array, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (DggArrayUtil.isNotEmpty(array)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return array;
    }

    /**
     * 断言给定数组是否不包含元素，数组必须为 {@code null} 或没有元素
     * <p>
     * <pre class="code">
     * DggAsserts.isEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param array 被检查的数组
     * @return 被检查的数组
     * @throws DggUtilException if the object array is not {@code null} and contains at least one elements
     */
    public static Object[] isEmpty(Object[] array) throws DggUtilException {
        return isEmpty(array, "[Assertion failed] - this array cannot contain elements: it must be null or have no elements");
    }

    /**
     * 断言给定集合为空
     * <p>
     * <pre class="code">
     * DggAsserts.isEmpty(collection, "Collection must have elements");
     * </pre>
     *
     * @param <T>              集合元素类型
     * @param collection       被检查的集合
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 非空集合
     * @throws DggUtilException if the collection is not {@code null} or contains at least one element
     */
    public static <T> Collection<T> isEmpty(Collection<T> collection, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (!CollectionUtils.isEmpty(collection)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return collection;
    }

    /**
     * 断言给定集合为空
     * <p>
     * <pre class="code">
     * DggAsserts.isEmpty(collection);
     * </pre>
     *
     * @param <T>        集合元素类型
     * @param collection 被检查的集合
     * @return 被检查集合
     * @throws DggUtilException if the collection is not {@code null} or contains at least one element
     */
    public static <T> Collection<T> isEmpty(Collection<T> collection) throws DggUtilException {
        return isEmpty(collection, "[Assertion failed] - this collection must be empty: it cannot contain elements");
    }

    /**
     * 断言给定Map为空
     * <p>
     * <pre class="code">
     * DggAsserts.isEmpty(map, "Map must have entries");
     * </pre>
     *
     * @param <K>              Key类型
     * @param <V>              Value类型
     * @param map              被检查的Map
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的Map
     * @throws DggUtilException if the map is not {@code null} or contains at least one element
     */
    public static <K, V> Map<K, V> isEmpty(Map<K, V> map, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (!CollectionUtils.isEmpty(map)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return map;
    }

    /**
     * 断言给定Map为空
     * <p>
     * <pre class="code">
     * DggAsserts.isEmpty(map, "Map must have entries");
     * </pre>
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param map 被检查的Map
     * @return 被检查的Map
     * @throws DggUtilException if the map is not {@code null} or contains at least one element
     */
    public static <K, V> Map<K, V> isEmpty(Map<K, V> map) throws DggUtilException {
        return isEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    //----------------------------------------------------------------------------------------------------------- Check not empty

    /**
     * 检查给定字符串是否为空，为空抛出 {@link DggUtilException}
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(name, "Name must not be empty");
     * </pre>
     *
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws DggUtilException 被检查字符串为空
     * @see MessageFormat#isNotEmpty(CharSequence)
     */
    public static String isNotEmpty(String text, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (DggStringUtil.isEmpty(text)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空，为空抛出 {@link DggUtilException}
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(name);
     * </pre>
     *
     * @param text 被检查字符串
     * @return 被检查的字符串
     * @throws DggUtilException 被检查字符串为空
     * @see MessageFormat#isNotEmpty(CharSequence)
     */
    public static String isNotEmpty(String text) throws DggUtilException {
        return isNotEmpty(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link DggUtilException}
     * <p>
     * <pre class="code">
     * DggAsserts.isNotBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param text             被检查字符串
     * @param errorMsgTemplate 错误消息模板，变量使用{}表示
     * @param params           参数
     * @return 非空字符串
     * @throws DggUtilException 被检查字符串为空白
     * @see MessageFormat#isNotBlank(CharSequence)
     */
    public static String isNotBlank(String text, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (StringUtils.isBlank(text)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return text;
    }

    /**
     * 检查给定字符串是否为空白（null、空串或只包含空白符），为空抛出 {@link DggUtilException}
     * <p>
     * <pre class="code">
     * DggAsserts.isNotBlank(name, "Name must not be blank");
     * </pre>
     *
     * @param text 被检查字符串
     * @return 非空字符串
     * @throws DggUtilException 被检查字符串为空白
     * @see MessageFormat#isNotBlank(CharSequence)
     */
    public static String isNotBlank(String text) throws DggUtilException {
        return isNotBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（既是否为子串）
     * <p>
     * <pre class="code">
     * DggAsserts.isNotContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch     被搜索的字符串
     * @param substring        被检查的子串
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的子串
     * @throws DggUtilException 非子串抛出异常
     */
    public static String isNotContain(String textToSearch, String substring, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (DggStringUtil.isNotEmpty(textToSearch) && DggStringUtil.isNotEmpty(substring) && textToSearch.contains(substring)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return substring;
    }

    /**
     * 断言给定字符串是否不被另一个字符串包含（既是否为子串）
     * <p>
     * <pre class="code">
     * DggAsserts.isNotContain(name, "rod", "Name must not contain 'rod'");
     * </pre>
     *
     * @param textToSearch 被搜索的字符串
     * @param substring    被检查的子串
     * @return 被检查的子串
     * @throws DggUtilException 非子串抛出异常
     */
    public static String isNotContain(String textToSearch, String substring) throws DggUtilException {
        return isNotContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [{}]", substring);
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param objects  被检查的数组
     * @param errorMsg 异常信息
     * @return 被检查的数组
     * @throws DggUtilException if the object array is {@code null} or has no elements
     */
    public static Object[] isNotEmpty(String errorMsg, Object... objects) throws DggUtilException {
        isNotNull(objects, errorMsg);
        for (Object object : objects) {
            isNotNull(object, errorMsg);
            if (object instanceof String) {
                String trim = object.toString().trim();
                isFalse("".equals(trim) || "null".equals(trim), errorMsg);
            }
        }
        return objects;
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param array            被检查的数组
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的数组
     * @throws DggUtilException if the object array is {@code null} or has no elements
     */
    public static Object[] isNotEmpty(Object[] array, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (DggArrayUtil.isEmpty(array)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return array;
    }

    /**
     * 断言给定数组是否包含元素，数组必须不为 {@code null} 且至少包含一个元素
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(array, "The array must have elements");
     * </pre>
     *
     * @param array 被检查的数组
     * @return 被检查的数组
     * @throws DggUtilException if the object array is {@code null} or has no elements
     */
    public static Object[] isNotEmpty(Object[] array) throws DggUtilException {
        return isNotEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
    }

    /**
     * 断言给定数组是否不包含{@code null}元素，如果数组为空或 {@code null}将被认为不包含
     * <p>
     * <pre class="code">
     * DggAsserts.isNoNullElements(array, "The array must have non-null elements");
     * </pre>
     *
     * @param <T>              数组元素类型
     * @param array            被检查的数组
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的数组
     * @throws DggUtilException if the object array contains a {@code null} element
     */
    public static <T> T[] isNoNullElements(T[] array, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (DggArrayUtil.hasNull(array)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return array;
    }

    /**
     * 断言给定数组是否不包含{@code null}元素，如果数组为空或 {@code null}将被认为不包含
     * <p>
     * <pre class="code">
     * DggAsserts.isNoNullElements(array);
     * </pre>
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 被检查的数组
     * @throws DggUtilException if the object array contains a {@code null} element
     */
    public static <T> T[] isNoNullElements(T[] array) throws DggUtilException {
        return isNoNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }

    /**
     * 断言给定集合非空
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(collection, "Collection must have elements");
     * </pre>
     *
     * @param <T>              集合元素类型
     * @param collection       被检查的集合
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 非空集合
     * @throws DggUtilException if the collection is {@code null} or has no elements
     */
    public static <T> Collection<T> isNotEmpty(Collection<T> collection, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (CollectionUtils.isEmpty(collection)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return collection;
    }

    /**
     * 断言给定集合非空
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(collection);
     * </pre>
     *
     * @param <T>        集合元素类型
     * @param collection 被检查的集合
     * @return 被检查集合
     * @throws DggUtilException if the collection is {@code null} or has no elements
     */
    public static <T> Collection<T> isNotEmpty(Collection<T> collection) throws DggUtilException {
        return isNotEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    /**
     * 断言给定Map非空
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(map, "Map must have entries");
     * </pre>
     *
     * @param <K>              Key类型
     * @param <V>              Value类型
     * @param map              被检查的Map
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查的Map
     * @throws DggUtilException if the map is {@code null} or has no entries
     */
    public static <K, V> Map<K, V> isNotEmpty(Map<K, V> map, String errorMsgTemplate, Object... params) throws DggUtilException {
        if (CollectionUtils.isEmpty(map)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return map;
    }

    /**
     * 断言给定Map非空
     * <p>
     * <pre class="code">
     * DggAsserts.isNotEmpty(map, "Map must have entries");
     * </pre>
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param map 被检查的Map
     * @return 被检查的Map
     * @throws DggUtilException if the map is {@code null} or has no entries
     */
    public static <K, V> Map<K, V> isNotEmpty(Map<K, V> map) throws DggUtilException {
        return isNotEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    /**
     * 断言给定对象是否是给定类的实例
     * <p>
     * <pre class="code">
     * DggAsserts.instanceOf(Foo.class, foo);
     * </pre>
     *
     * @param <T>  被检查对象泛型类型
     * @param type 被检查对象匹配的类型
     * @param obj  被检查对象
     * @return 被检查的对象
     * @throws DggUtilException if the object is not an instance of clazz
     * @see Class#isInstance(Object)
     */
    public static <T> T isInstanceOf(Class<?> type, T obj) {
        return isInstanceOf(type, obj, "Object [{0}] is not instanceof [{1}]", obj, type);
    }

    /**
     * 断言给定对象是否是给定类的实例
     * <p>
     * <pre class="code">
     * DggAsserts.isInstanceOf(Foo.class, foo);
     * </pre>
     *
     * @param <T>              被检查对象泛型类型
     * @param type             被检查对象匹配的类型
     * @param obj              被检查对象
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 被检查对象
     * @throws DggUtilException if the object is not an instance of clazz
     * @see Class#isInstance(Object)
     */
    public static <T> T isInstanceOf(Class<?> type, T obj, String errorMsgTemplate, Object... params) throws DggUtilException {
        isNotNull(type, "Type to check against must not be null");
        if (false == type.isInstance(obj)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
        return obj;
    }

    /**
     * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
     * <p>
     * <pre class="code">
     * DggAsserts.isAssignable(Number.class, myClass);
     * </pre>
     *
     * @param superType the super type to check
     * @param subType   the sub type to check
     * @throws DggUtilException if the classes are not assignable
     */
    public static void isAssignable(Class<?> superType, Class<?> subType) throws DggUtilException {
        isAssignable(superType, subType, "{0} is not assignable to {1})", subType, superType);
    }

    /**
     * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
     * <p>
     * <pre class="code">
     * DggAsserts.isAssignable(Number.class, myClass);
     * </pre>
     *
     * @param superType        the super type to check against
     * @param subType          the sub type to check
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @throws DggUtilException if the classes are not assignable
     */
    public static void isAssignable(Class<?> superType, Class<?> subType, String errorMsgTemplate, Object... params) throws DggUtilException {
        isNotNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new DggUtilException(MessageFormat.format(errorMsgTemplate, params));
        }
    }

    /**
     * 检查下标（数组、集合、字符串）是否符合要求，下标必须满足：
     *
     * <pre>
     * 0 <= index < size
     * </pre>
     *
     * @param index 下标
     * @param size  长度
     * @return 检查后的下标
     * @throws IllegalArgumentException  如果size < 0 抛出此异常
     * @throws IndexOutOfBoundsException 如果index < 0或者 index >= size 抛出此异常
     */
    public static int checkIndex(int index, int size) throws IllegalArgumentException, IndexOutOfBoundsException {
        return checkIndex(index, size, "[Assertion failed]");
    }

    /**
     * 检查下标（数组、集合、字符串）是否符合要求，下标必须满足：
     *
     * <pre>
     * 0 <= index < size
     * </pre>
     *
     * @param index            下标
     * @param size             长度
     * @param errorMsgTemplate 异常时的消息模板
     * @param params           参数列表
     * @return 检查后的下标
     * @throws IllegalArgumentException  如果size < 0 抛出此异常
     * @throws IndexOutOfBoundsException 如果index < 0或者 index >= size 抛出此异常
     */
    public static int checkIndex(int index, int size, String errorMsgTemplate, Object... params) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(badIndexMsg(index, size, errorMsgTemplate, params));
        }
        return index;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     */
    public static int checkBetween(int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(MessageFormat.format("Length must be between {0} and {1}.", min, max));
        }
        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     */
    public static long checkBetween(long value, long min, long max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(MessageFormat.format("Length must be between {0} and {1}.", min, max));
        }
        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     */
    public static double checkBetween(double value, double min, double max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(MessageFormat.format("Length must be between {0} and {1}.", min, max));
        }
        return value;
    }

    /**
     * 检查值是否在指定范围内
     *
     * @param value 值
     * @param min   最小值（包含）
     * @param max   最大值（包含）
     * @return 检查后的长度值
     */
    public static Number checkBetween(Number value, Number min, Number max) {
        isNotNull(value);
        isNotNull(min);
        isNotNull(max);
        double valueDouble = value.doubleValue();
        double minDouble = min.doubleValue();
        double maxDouble = max.doubleValue();
        if (valueDouble < minDouble || valueDouble > maxDouble) {
            throw new IllegalArgumentException(MessageFormat.format("Length must be between {0} and {1}.", min, max));
        }
        return value;
    }


    /**
     * 构造异常信息
     *
     * @param errorMsg
     * @param <T>
     */
    public static <T extends RuntimeException> boolean build(Class<T> exceptionClass, String errorMsg) {
        return getResult(false, exceptionClass, errorMsg == null ? "操作失败" : errorMsg);
    }

    // -------------------------------------------------------------------------------------------------------------------------------------------- Private method start

    /**
     * 错误的下标时显示的消息
     *
     * @param index  下标
     * @param size   长度
     * @param desc   异常时的消息模板
     * @param params 参数列表
     * @return 消息
     */
    protected static String badIndexMsg(int index, int size, String desc, Object... params) {
        if (index < 0) {
            return MessageFormat.format("{0} ({1}) must not be negative", MessageFormat.format(desc, params), index);
        } else if (size < 0) {
            throw new IllegalArgumentException("negative size: " + size);
        } else { // index >= size
            return MessageFormat.format("{0} ({1}) must be less than size ({2})", MessageFormat.format(desc, params), index, size);
        }
    }

    /**
     * 处理验证结果的私有方法，减少重复代码量
     *
     * @param result         结果本身
     * @param exceptionClass 需要抛出的异常类实例
     * @param errorMsg       需要返回的消息
     * @param <T>            需要抛出的异常类类型
     * @return 结果
     */
    private static <T extends RuntimeException> boolean getResult(boolean result, Class<T> exceptionClass, String errorMsg) {
        // 如果结果为false，并且需要抛出异常
        if (!result && exceptionClass != null) {
            throw buildException(exceptionClass, errorMsg);
        }
        return result;
    }

    /**
     * 构造异常
     *
     * @param exceptionClass 需要抛出的异常类实例
     * @param errorMsg       需要返回的消息
     * @param <T>            需要抛出的异常类类型
     * @return
     */
    private static <T extends RuntimeException> T buildException(Class<T> exceptionClass, String errorMsg) {
        T exception = null;
        Constructor constructor = null;
        try {
            constructor = exceptionClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw new DggUtilException("缺少String为参数的构造函数", e);
        }
        try {
            exception = (T) constructor.newInstance(errorMsg);
        } catch (InstantiationException e) {
            throw new DggUtilException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new DggUtilException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new DggUtilException(e.getMessage(), e);
        }
        return exception;
    }
}
