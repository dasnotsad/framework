package net.dgg.utils.core;


import com.google.common.collect.Iterables;
import net.dgg.utils.exception.DggUtilException;
import org.springframework.cglib.core.ReflectUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Describe: Map相关工具类
 * Created by yan.x on 2019-01-24 .
 **/
public final class DggMapUtil {

    /**
     * 默认初始大小
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    /**
     * 默认增长因子，当Map的size达到 容量*增长因子时，开始扩充Map
     */
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Map是否为空
     *
     * @param map 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return null != map && false == map.isEmpty();
    }

    // ----------------------------------------------------------------------------------------------- new HashMap

    /**
     * 新建一个HashMap并put一个值
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return HashMap对象
     */
    public static <K, V> Map<K, V> put(K key, V value) {
        Map<K, V> map = newHashMap(DEFAULT_INITIAL_CAPACITY);
        map.put(key, value);
        return map;
    }

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return HashMap对象
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<K, V>(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>     Key类型
     * @param <V>     Value类型
     * @param size    初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75
     * @param isOrder Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     */
    public static <K, V> Map<K, V> newHashMap(int size, boolean isOrder) {
        int initialCapacity = (int) (size / DEFAULT_LOAD_FACTOR);
        return isOrder ? new LinkedHashMap<K, V>(initialCapacity) : new HashMap<K, V>(initialCapacity);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>  Key类型
     * @param <V>  Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75
     * @return HashMap对象
     */
    public static <K, V> Map<K, V> newHashMap(int size) {
        return newHashMap(size, false);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>     Key类型
     * @param <V>     Value类型
     * @param isOrder Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     */
    public static <K, V> Map<K, V> newHashMap(boolean isOrder) {
        return newHashMap(DEFAULT_INITIAL_CAPACITY, isOrder);
    }

    /**
     * 新建TreeMap，Key有序的Map
     *
     * @param comparator Key比较器
     * @return TreeMap
     */
    public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> comparator) {
        return new TreeMap<>(comparator);
    }

    /**
     * 新建TreeMap，Key有序的Map
     *
     * @param map        Map
     * @param comparator Key比较器
     * @return TreeMap
     */
    public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map, Comparator<? super K> comparator) {
        final TreeMap<K, V> treeMap = new TreeMap<>(comparator);
        if (false == isEmpty(map)) {
            treeMap.putAll(map);
        }
        return treeMap;
    }

    /**
     * 创建Map<br>
     * 传入抽象Map{@link AbstractMap}和{@link Map}类将默认创建{@link HashMap}
     *
     * @param <K>     map键类型
     * @param <V>     map值类型
     * @param mapType map类型
     * @return {@link Map}实例
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> createMap(Class<?> mapType) {
        if (mapType.isAssignableFrom(AbstractMap.class)) {
            int initialCapacity = (int) (DEFAULT_INITIAL_CAPACITY / DEFAULT_LOAD_FACTOR);
            return new HashMap<>(initialCapacity);
        } else {
            try {
                return (Map<K, V>) ReflectUtils.newInstance(mapType);
            } catch (Exception e) {
                throw new DggUtilException(e);
            }
        }
    }

    /**
     * Constructs an empty LinkedHashMap.
     */
    public static final <k, v> LinkedHashMap<k, v> newLinkedHashMap() {
        return new LinkedHashMap<k, v>();
    }
    // ----------------------------------------------------------------------------------------------- value of

    /**
     * 将单一键值对转换为Map
     *
     * @param <K>   键类型
     * @param <V>   值类型
     * @param key   键
     * @param value 值
     * @return {@link HashMap}
     */
    public static <K, V> Map<K, V> of(K key, V value) {
        return of(key, value, false);
    }

    /**
     * 将单一键值对转换为Map
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param key     键
     * @param value   值
     * @param isOrder 是否有序
     * @return {@link HashMap}
     */
    public static <K, V> Map<K, V> of(K key, V value, boolean isOrder) {
        final Map<K, V> map = newHashMap(isOrder);
        map.put(key, value);
        return map;
    }

    /**
     * 将数组转换为Map（HashMap），支持数组元素类型为：
     *
     * <pre>
     * Map.Entry
     * 长度大于1的数组（取前两个值），如果不满足跳过此元素
     * Iterable 长度也必须大于1（取前两个值），如果不满足跳过此元素
     * Iterator 长度也必须大于1（取前两个值），如果不满足跳过此元素
     * </pre>
     *
     * <pre>
     * Map&lt;Object, Object&gt; colorMap = MapUtil.of(new String[][] {{
     *     {"RED", "#FF0000"},
     *     {"GREEN", "#00FF00"},
     *     {"BLUE", "#0000FF"}});
     * </pre>
     * <p>
     * 参考：commons-lang
     *
     * @param array 数组。元素类型为Map.Entry、数组、Iterable、Iterator
     * @return {@link HashMap}
     */
    @SuppressWarnings("rawtypes")
    public static HashMap<Object, Object> of(Object[] array) {
        if (array == null) {
            return null;
        }
        final HashMap<Object, Object> map = new HashMap<>((int) (array.length * 1.5));
        for (int i = 0; i < array.length; i++) {
            Object object = array[i];
            if (object instanceof Map.Entry) {
                Entry entry = (Entry) object;
                map.put(entry.getKey(), entry.getValue());
            } else if (object instanceof Object[]) {
                final Object[] entry = (Object[]) object;
                if (entry.length > 1) {
                    map.put(entry[0], entry[1]);
                }
            } else if (object instanceof Iterable) {
                Iterator iter = ((Iterable) object).iterator();
                if (iter.hasNext()) {
                    final Object key = iter.next();
                    if (iter.hasNext()) {
                        final Object value = iter.next();
                        map.put(key, value);
                    }
                }
            } else if (object instanceof Iterator) {
                Iterator iter = ((Iterator) object);
                if (iter.hasNext()) {
                    final Object key = iter.next();
                    if (iter.hasNext()) {
                        final Object value = iter.next();
                        map.put(key, value);
                    }
                }
            } else {
                throw new IllegalArgumentException(MessageFormat.format("Array element {0}, '{1}', is not type of Map.Entry or Array or Iterable or Iterator", i, object));
            }
        }
        return map;
    }

    /**
     * 行转列，合并相同的键，值合并为列表<br>
     * 将Map列表中相同key的值组成列表做为Map的value<br>
     * 是{@link #toMapList(Map)}的逆方法<br>
     * 比如传入数据：
     *
     * <pre>
     * [
     *  {a: 1, b: 1, c: 1}
     *  {a: 2, b: 2}
     *  {a: 3, b: 3}
     *  {a: 4}
     * ]
     * </pre>
     * <p>
     * 结果是：
     *
     * <pre>
     * {
     *   a: [1,2,3,4]
     *   b: [1,2,3,]
     *   c: [1]
     * }
     * </pre>
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param mapList Map列表
     * @return Map
     */
    public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
        final HashMap<K, List<V>> resultMap = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        if (Iterables.isEmpty(mapList)) {
            return resultMap;
        }

        Set<Entry<K, V>> entrySet;
        for (Map<K, V> map : mapList) {
            entrySet = map.entrySet();
            K key;
            List<V> valueList;
            for (Entry<K, V> entry : entrySet) {
                key = entry.getKey();
                valueList = resultMap.get(key);
                if (null == valueList) {
                    valueList = new ArrayList<>();
                    valueList.add(entry.getValue());
                    resultMap.put(key, valueList);
                } else {
                    valueList.add(entry.getValue());
                }
            }
        }

        return resultMap;
    }

    /**
     * 列转行。将Map中值列表分别按照其位置与key组成新的map。<br>
     * 是{@link #toListMap(Iterable)}的逆方法<br>
     * 比如传入数据：
     *
     * <pre>
     * {
     *   a: [1,2,3,4]
     *   b: [1,2,3,]
     *   c: [1]
     * }
     * </pre>
     * <p>
     * 结果是：
     *
     * <pre>
     * [
     *  {a: 1, b: 1, c: 1}
     *  {a: 2, b: 2}
     *  {a: 3, b: 3}
     *  {a: 4}
     * ]
     * </pre>
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param listMap 列表Map
     * @return Map列表
     */
    public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
        final List<Map<K, V>> resultList = new ArrayList<>();
        if (isEmpty(listMap)) {
            return resultList;
        }

        boolean isEnd = true;
        int index = 0;
        Map<K, V> map;
        do {
            isEnd = true;
            map = new HashMap<>();
            List<V> vList;
            int vListSize;
            for (Entry<K, ? extends Iterable<V>> entry : listMap.entrySet()) {
                vList = new ArrayList<>();
                Iterator<V> iterator = entry.getValue().iterator();
                if (null != iterator) {
                    while (iterator.hasNext()) {
                        vList.add(iterator.next());
                    }
                }
                vListSize = vList.size();
                if (index < vListSize) {
                    map.put(entry.getKey(), vList.get(index));
                    if (index != vListSize - 1) {
                        // 当值列表中还有更多值（非最后一个），继续循环
                        isEnd = false;
                    }
                }
            }
            if (false == map.isEmpty()) {
                resultList.add(map);
            }
            index++;
        } while (false == isEnd);

        return resultList;
    }

    /**
     * 将键值对转换为二维数组，第一维是key，第二纬是value
     *
     * @param map Map<?, ?> map
     * @return 数组
     */
    public static Object[][] toObjectArray(Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        final Object[][] result = new Object[map.size()][2];
        if (map.isEmpty()) {
            return result;
        }
        int index = 0;
        for (Entry<?, ?> entry : map.entrySet()) {
            result[index][0] = entry.getKey();
            result[index][1] = entry.getValue();
            index++;
        }
        return result;
    }

    // ----------------------------------------------------------------------------------------------- join

    /**
     * 将map转成字符串
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @return 连接字符串
     */
    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator) {
        return join(map, separator, keyValueSeparator, false);
    }

    /**
     * 将map转成字符串，忽略null的键和值
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @return 连接后的字符串
     */
    public static <K, V> String joinIgnoreNull(Map<K, V> map, String separator, String keyValueSeparator) {
        return join(map, separator, keyValueSeparator, true);
    }

    /**
     * 将map转成字符串
     *
     * @param <K>               键类型
     * @param <V>               值类型
     * @param map               Map
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @param isIgnoreNull      是否忽略null的键和值
     * @return 连接后的字符串
     */
    public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, boolean isIgnoreNull) {
        final StringBuilder strBuilder = new StringBuilder();
        boolean isFirst = true;
        for (Entry<K, V> entry : map.entrySet()) {
            if (false == isIgnoreNull || entry.getKey() != null && entry.getValue() != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    strBuilder.append(separator);
                }
                strBuilder.append(entry.getKey()).append(keyValueSeparator).append(entry.getValue());
            }
        }
        return strBuilder.toString();
    }

    // ----------------------------------------------------------------------------------------------- filter

    /**
     * 逆转Map的key和value
     *
     * @param <K> 键类型，目标的值类型
     * @param <V> 值类型，目标的键类型
     * @param map 被转换的Map
     * @return 逆转后的Map
     * @deprecated 请使用{@link MapUtil#reverse(Map)} 代替
     */
    @Deprecated
    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        Map<V, K> inverseMap;
        if (map instanceof LinkedHashMap) {
            inverseMap = new LinkedHashMap<>(map.size());
        } else if (map instanceof TreeMap) {
            inverseMap = new TreeMap<>();
        } else {
            inverseMap = new HashMap<>(map.size());
        }

        for (Entry<K, V> entry : map.entrySet()) {
            inverseMap.put(entry.getValue(), entry.getKey());
        }
        return inverseMap;
    }

    /**
     * 排序已有Map，Key有序的Map，使用默认Key排序方式（字母顺序）
     *
     * @param map Map
     * @return TreeMap
     * @see #newTreeMap(Map, Comparator)
     * @since 4.0.1
     */
    public static <K, V> TreeMap<K, V> sort(Map<K, V> map) {
        return sort(map, null);
    }

    /**
     * 排序已有Map，Key有序的Map
     *
     * @param map        Map
     * @param comparator Key比较器
     * @return TreeMap
     * @see #newTreeMap(Map, Comparator)
     * @since 4.0.1
     */
    public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
        TreeMap<K, V> result;
        if (map instanceof TreeMap) {
            // 已经是可排序Map，此时只有比较器一致才返回原map
            result = (TreeMap<K, V>) map;
            if (null == comparator || comparator.equals(result.comparator())) {
                return result;
            }
        } else {
            result = newTreeMap(map, comparator);
        }

        return result;
    }


    // ----------------------------------------------------------------------------------------------- builder

    /**
     * 获取Map指定key的值，并转换为字符串
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static String getStr(Map<?, ?> map, Object key) {
        return get(map, key, String.class);
    }

    /**
     * 获取Map指定key的值，并转换为Integer
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Integer getInt(Map<?, ?> map, Object key) {
        return get(map, key, Integer.class);
    }

    /**
     * 获取Map指定key的值，并转换为Double
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Double getDouble(Map<?, ?> map, Object key) {
        return get(map, key, Double.class);
    }

    /**
     * 获取Map指定key的值，并转换为Float
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Float getFloat(Map<?, ?> map, Object key) {
        return get(map, key, Float.class);
    }

    /**
     * 获取Map指定key的值，并转换为Short
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Short getShort(Map<?, ?> map, Object key) {
        return get(map, key, Short.class);
    }

    /**
     * 获取Map指定key的值，并转换为Bool
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Boolean getBool(Map<?, ?> map, Object key) {
        return get(map, key, Boolean.class);
    }

    /**
     * 获取Map指定key的值，并转换为Character
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Character getChar(Map<?, ?> map, Object key) {
        return get(map, key, Character.class);
    }

    /**
     * 获取Map指定key的值，并转换为Long
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.0.6
     */
    public static Long getLong(Map<?, ?> map, Object key) {
        return get(map, key, Long.class);
    }

    /**
     * 获取Map指定key的值，并转换为{@link Date}
     *
     * @param map Map
     * @param key 键
     * @return 值
     * @since 4.1.2
     */
    public static Date getDate(Map<?, ?> map, Object key) {
        return get(map, key, Date.class);
    }

    /**
     * 获取Map指定key的值，并转换为指定类型
     *
     * @param <T>  目标值类型
     * @param map  Map
     * @param key  键
     * @param type 值类型
     * @return 值
     * @since 4.0.6
     */
    public static <T> T get(Map<?, ?> map, Object key, Class<T> type) {
        return null == map ? null : (T) map.get(key);
    }
}
