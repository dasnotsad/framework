package net.dgg.utils.core;

import net.dgg.utils.string.DggStringUtil;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @Description 自定义迭代器, 扩展{@link Iterable}
 * @Version 1.0.0
 * @Author Created by yan.x on 2019-04-29 .
 * @Copyright © dgg group.All Rights Reserved.
 **/
public class Iterables {

    /**
     * 扩展forEach 方法,添加循环体索引
     *
     * @param elements : 迭代器对象
     * @param action   : 执行体
     * @param <E>
     */
    public static <E> void forEach(Iterable<? extends E> elements, BiConsumer<Integer, ? super E> action) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(action);
        int index = 0;
        for (E element : elements) {
            action.accept(index++, element);
        }
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty(Iterator<?> Iterator) {
        return null == Iterator || false == Iterator.hasNext();
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterable<?> iterable) {
        return null != iterable && isNotEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterator<?> Iterator) {
        return null != Iterator && Iterator.hasNext();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iter 被检查的{@link Iterable}对象，如果为{@code null} 返回false
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull(Iterable<?> iter) {
        return hasNull(null == iter ? null : iter.iterator());
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iter 被检查的{@link Iterator}对象，如果为{@code null} 返回false
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull(Iterator<?> iter) {
        if (null == iter) {
            return true;
        }
        while (iter.hasNext()) {
            if (null == iter.next()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否全部元素为null
     *
     * @param iter iter 被检查的{@link Iterable}对象，如果为{@code null} 返回true
     * @return 是否全部元素为null
     */
    public static boolean isAllNull(Iterable<?> iter) {
        return isAllNull(null == iter ? null : iter.iterator());
    }

    /**
     * 是否全部元素为null
     *
     * @param iter iter 被检查的{@link Iterator}对象，如果为{@code null} 返回true
     * @return 是否全部元素为null
     */
    public static boolean isAllNull(Iterator<?> iter) {
        if (null == iter) {
            return true;
        }

        while (iter.hasNext()) {
            if (null != iter.next()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}<br>
     * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value<br>
     * 例如：[a,b,c,c,c] 得到：<br>
     * a: 1<br>
     * b: 1<br>
     * c: 3<br>
     *
     * @param <T>  集合元素类型
     * @param iter {@link Iterable}，如果为null返回一个空的Map
     * @return {@link Map}
     */
    public static <T> Map<T, Integer> countMap(Iterable<T> iter) {
        return countMap(null == iter ? null : iter.iterator());
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}<br>
     * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value<br>
     * 例如：[a,b,c,c,c] 得到：<br>
     * a: 1<br>
     * b: 1<br>
     * c: 3<br>
     *
     * @param <T>  集合元素类型
     * @param iter {@link Iterator}，如果为null返回一个空的Map
     * @return {@link Map}
     */
    public static <T> Map<T, Integer> countMap(Iterator<T> iter) {
        final HashMap<T, Integer> countMap = new HashMap<>();
        if (null != iter) {
            Integer count;
            T t;
            while (iter.hasNext()) {
                t = iter.next();
                count = countMap.get(t);
                if (null == count) {
                    countMap.put(t, 1);
                } else {
                    countMap.put(t, count + 1);
                }
            }
        }
        return countMap;
    }

    /**
     * 字段值与列表值对应的Map，常用于元素对象中有唯一ID时需要按照这个ID查找对象的情况<br>
     * 例如：车牌号 =》车
     *
     * @param <K>       字段名对应值得类型，不确定请使用Object
     * @param <V>       对象类型
     * @param iter      对象列表
     * @param fieldName 字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     */
    public static <K, V> Map<K, V> fieldValueMap(Iterable<V> iter, String fieldName) {
        return fieldValueMap(null == iter ? null : iter.iterator(), fieldName);
    }

    /**
     * 字段值与列表值对应的Map，常用于元素对象中有唯一ID时需要按照这个ID查找对象的情况<br>
     * 例如：车牌号 =》车
     *
     * @param <K>       字段名对应值得类型，不确定请使用Object
     * @param <V>       对象类型
     * @param iter      对象列表
     * @param fieldName 字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     */
    public static <K, V> Map<K, V> fieldValueMap(Iterator<V> iter, String fieldName) {
        final Map<K, V> result = new HashMap<>();
        if (null != iter) {
            V value;
            while (iter.hasNext()) {
                value = iter.next();
                result.put((K) DggBeanUtil.getFieldValue(value, fieldName), value);
            }
        }
        return result;
    }

    /**
     * 两个字段值组成新的Map
     *
     * @param <K>               字段名对应值得类型，不确定请使用Object
     * @param <V>               值类型，不确定使用Object
     * @param iter              对象列表
     * @param fieldNameForKey   做为键的字段名（会通过反射获取其值）
     * @param fieldNameForValue 做为值的字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     */
    public static <K, V> Map<K, V> fieldValueAsMap(Iterator<?> iter, String fieldNameForKey, String fieldNameForValue) {
        final Map<K, V> result = new HashMap<>();
        if (null != iter) {
            Object value;
            while (iter.hasNext()) {
                value = iter.next();
                result.put((K) DggBeanUtil.getFieldValue(value, fieldNameForKey), (V) DggBeanUtil.getFieldValue(value, fieldNameForValue));
            }
        }
        return result;
    }

    /**
     * 获取指定Bean列表中某个字段，生成新的列表
     *
     * @param <V>       对象类型
     * @param iter      对象列表
     * @param fieldName 字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     */
    public static <V> List<Object> fieldValueList(Iterator<V> iter, String fieldName) {
        final List<Object> result = new ArrayList<>();
        if (null != iter) {
            V value;
            while (iter.hasNext()) {
                value = iter.next();
                result.add(DggBeanUtil.getFieldValue(value, fieldName));
            }
        }
        return result;
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(Iterable<T> iterable, CharSequence conjunction) {
        if (null == iterable) {
            return null;
        }
        return join(iterable.iterator(), conjunction);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀，null表示不添加
     * @param suffix      每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     */
    public static <T> String join(Iterable<T> iterable, CharSequence conjunction, String prefix, String suffix) {
        if (null == iterable) {
            return null;
        }
        return join(iterable.iterator(), conjunction, prefix, suffix);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
        return join(iterator, conjunction, null, null);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀，null表示不添加
     * @param suffix      每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     */
    public static <T> String join(Iterator<T> iterator, CharSequence conjunction, String prefix, String suffix) {
        if (null == iterator) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        T item;
        while (iterator.hasNext()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }

            item = iterator.next();
            if (DggArrayUtil.isArray(item)) {
                sb.append(DggArrayUtil.join(DggArrayUtil.wrap(item), conjunction, prefix, suffix));
            } else if (item instanceof Iterable<?>) {
                sb.append(join((Iterable<?>) item, conjunction, prefix, suffix));
            } else if (item instanceof Iterator<?>) {
                sb.append(join((Iterator<?>) item, conjunction, prefix, suffix));
            } else {
                sb.append(DggStringUtil.wrap(String.valueOf(item), prefix, suffix));
            }
        }
        return sb.toString();
    }

    /**
     * 将Entry集合转换为HashMap
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param entryIter entry集合
     * @return Map
     */
    public static <K, V> HashMap<K, V> toMap(Iterable<Map.Entry<K, V>> entryIter) {
        final HashMap<K, V> map = new HashMap<K, V>();
        if (isNotEmpty(entryIter)) {
            for (Map.Entry<K, V> entry : entryIter) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return 标题内容Map
     */
    public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values) {
        return toMap(keys, values, false);
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return 标题内容Map
     */
    public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values, boolean isOrder) {
        return toMap(null == keys ? null : keys.iterator(), null == values ? null : values.iterator(), isOrder);
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return 标题内容Map
     */
    public static <K, V> Map<K, V> toMap(Iterator<K> keys, Iterator<V> values) {
        return toMap(keys, values, false);
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return 标题内容Map
     */
    public static <K, V> Map<K, V> toMap(Iterator<K> keys, Iterator<V> values, boolean isOrder) {
        final Map<K, V> resultMap = DggMapUtil.newHashMap(isOrder);
        if (isNotEmpty(keys)) {
            while (keys.hasNext()) {
                resultMap.put(keys.next(), (null != values && values.hasNext()) ? values.next() : null);
            }
        }
        return resultMap;
    }

    /**
     * Iterator转List<br>
     * 不判断，直接生成新的List
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return List
     */
    public static <E> List<E> toList(Iterable<E> iter) {
        return toList(iter.iterator());
    }

    /**
     * Iterator转List<br>
     * 不判断，直接生成新的List
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return List
     */
    public static <E> List<E> toList(Iterator<E> iter) {
        final List<E> list = new ArrayList<>();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list;
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return 第一个元素
     */
    public static <T> T getFirst(Iterable<T> iterable) {
        if (null != iterable) {
            return getFirst(iterable.iterator());
        }
        return null;
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个元素
     */
    public static <T> T getFirst(Iterator<T> iterator) {
        if (null != iterator && iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
