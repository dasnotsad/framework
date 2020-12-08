package net.dgg.utils.desensitization;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/9/17
 * @Description: 脱敏工具类
 */
public class DggDesensitizationUtil {

    private static final int TEN_LENGTH = 10;
    private static final int THREE_LENGTH = 3;

    /**
     * 数据脱敏
     *
     * @param content 待脱敏数据
     * @return 脱敏后的数据
     */
    public static String desensitizationContent(String content) {
        return desensitizationContent(content, false);
    }

    /**
     * 数据脱敏
     *
     * @param content     待脱敏数据
     * @param matchLength 是否大于10个长度的数据进行星号补全
     * @return 脱敏后的数据
     */
    public static String desensitizationContent(String content, boolean matchLength) {
        if (content == null || content.length() == 0) {
            return "";
        }
        int length = content.length();
        String result;
        if (length > TEN_LENGTH) {
            if (matchLength) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i <= (content.length() - 5); i++) {
                    builder.append("*");
                }
                result = content.replaceAll("(\\w{3})\\w{" + (content.length() - 5) + "}(\\w{2})", "$1" + builder.toString() + "$2");
            } else {
                result = content.replaceAll("(\\w{3})\\w{" + (content.length() - 5) + "}(\\w{2})", "$1******$2");
            }
        } else if (length > THREE_LENGTH && length <= TEN_LENGTH) {
            result = content.replaceAll("(\\w{" + (content.length() - 4) + "})\\w{4}", "$1****");
        } else if (length > 1 && length <= THREE_LENGTH) {
            result = content.replaceAll("(\\w{1})\\w{" + (content.length() - 1) + "}", "$1*");
        } else {
            result = "*";
        }
        return result;
    }


    /**
     * 对象集合脱敏
     *
     * @param columnList 对象集合
     * @param props      对象属性
     * @param <T>
     * @return
     */
    public static <T> List<T> desensitizationDataList(List<T> columnList, String... props) {
        if (columnList == null
                || props == null
                || columnList.size() == 0
                || props.length == 0) {
            return columnList;
        }
        List<T> columnListRe = new ArrayList<>(columnList.size());
        for (T t : columnList) {
            columnListRe.add(desensitizationData(t, props));
        }
        return columnListRe;
    }

    /**
     * 对象脱敏
     *
     * @param t     对象
     * @param props 对象属性
     * @param <T>
     * @return
     */
    public static <T> T desensitizationData(T t, String... props) {
        if (t == null || props == null || props.length == 0) {
            return t;
        }
        for (String prop : props) {
            try {
                Field field = t.getClass().getDeclaredField(prop);
                field.setAccessible(true);
                String value = field.get(t).toString();
                field.set(t, desensitizationContent(value));
            } catch (IllegalAccessException e) {
                continue;
            } catch (NoSuchFieldException e) {
                continue;
            }
        }
        return t;
    }
}
