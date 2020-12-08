package net.dasnotsad.framework.tac.elasticsearch.utils;

import java.lang.reflect.Array;

/**
 * @Author: wangxianwei
 * @Date: 2020/4/20
 * @Description: 数组工具类
 */
public final class ArrayUtil {


    /**
     * <p>将新元素添加到已有数组中</p>
     * <p>添加新元素会生成一个新的数组，不影响原数组</p>
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    @SafeVarargs
    public static <T> T[] append(final T[] buffer, T... newElements) {
        if (isEmpty(newElements)) {
            return buffer;
        }

        T[] t = resize(buffer, buffer.length + newElements.length);
        System.arraycopy(newElements, 0, t, buffer.length, newElements.length);
        return t;
    }

    //---------------------------------------------------------------------- indexof


    //---------------------------------------------------------------------- resize

    /**
     * <p>生成一个新的重新设置大小的数组</p>
     * <p>新数组的类型为原数组的类型，调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，缩小则截断</p>
     *
     * @param <T>     数组元素类型
     * @param buffer  原数组
     * @param newSize 新的数组大小
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] buffer, int newSize) {
        return resize(buffer, newSize, buffer.getClass().getComponentType());
    }

    /**
     * 生成一个新的重新设置大小的数组<br>
     * 调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，缩小则截断
     *
     * @param <T>           数组元素类型
     * @param buffer        原数组
     * @param newSize       新的数组大小
     * @param componentType 数组元素类型
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] buffer, int newSize, Class<?> componentType) {
        T[] newArray = newArray(componentType, newSize);
        if (isNotEmpty(buffer)) {
            System.arraycopy(buffer, 0, newArray, 0, Math.min(buffer.length, newSize));
        }
        return newArray;
    }

    /**
     * <p>新建一个空数组</p>
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(final Class<?> componentType, final int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }

    public static <T> boolean isNotEmpty(final T[] array) {
        return !isEmpty(array);
    }

    public static boolean isEmpty(final Object[] array) {
        return getLength(array) == 0;
    }

    public static int getLength(final Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }
}