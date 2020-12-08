package net.dgg.utils.core;


import net.dgg.utils.exception.DggUtilException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 集合相关工具类<p>
 * 此工具方法针对{@link Collection}及其实现类封装的工具。<p>
 * 由于{@link Collection} 实现了{@link Iterable}接口
 * <p>
 * Created by yan.x on 2019-01-24 .
 **/
public final class DggCollectionUtil {

    /**
     * 两个集合的并集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最多的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c, c]，此结果中只保留了三个c
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 并集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> union(final Collection<T> coll1, final Collection<T> coll2) {
        final ArrayList<T> list = new ArrayList<>();
        if (isEmpty(coll1)) {
            list.addAll(coll2);
        } else if (isEmpty(coll2)) {
            list.addAll(coll1);
        } else {
            final Map<T, Integer> map1 = countMap(coll1);
            final Map<T, Integer> map2 = countMap(coll2);
            final Set<T> elts = newHashSet(coll2);
            elts.addAll(coll1);
            int m;
            for (T t : elts) {
                m = Math.max(map1.get(t), map2.get(t));
                for (int i = 0; i < m; i++) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    /**
     * 多个集合的并集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最多的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c, c]，此结果中只保留了三个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link ArrayList}
     */
    @SafeVarargs
    public static <T> Collection<T> union(final Collection<T> coll1, final Collection<T> coll2, final Collection<T>... otherColls) {
        Collection<T> union = union(coll1, coll2);
        for (Collection<T> coll : otherColls) {
            union = union(union, coll);
        }
        return union;
    }

    /**
     * 两个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最少的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c]，此结果中只保留了两个c
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 交集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> intersection(final Collection<T> coll1, final Collection<T> coll2) {
        final ArrayList<T> list = new ArrayList<>();
        if (isNotEmpty(coll1) && isNotEmpty(coll2)) {
            final Map<T, Integer> map1 = countMap(coll1);
            final Map<T, Integer> map2 = countMap(coll2);
            final Set<T> elts = newHashSet(coll2);
            int m;
            for (T t : elts) {
                m = Math.min(map1.get(t), map2.get(t));
                for (int i = 0; i < m; i++) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    /**
     * 多个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最少的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c]，此结果中只保留了两个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link ArrayList}
     */
    @SafeVarargs
    public static <T> Collection<T> intersection(final Collection<T> coll1, final Collection<T> coll2, final Collection<T>... otherColls) {
        Collection<T> intersection = intersection(coll1, coll2);
        if (isEmpty(intersection)) {
            return intersection;
        }
        for (Collection<T> coll : otherColls) {
            intersection = intersection(intersection, coll);
            if (isEmpty(intersection)) {
                return intersection;
            }
        }
        return intersection;
    }

    /**
     * 两个集合的差集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留两个集合中此元素个数差的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[c]，此结果中只保留了一个<br>
     * 任意一个集合为空，返回另一个集合<br>
     * 两个集合无交集则返回两个集合的组合
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 差集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> disjunction(final Collection<T> coll1, final Collection<T> coll2) {
        if (isEmpty(coll1)) {
            return coll2;
        }
        if (isEmpty(coll2)) {
            return coll1;
        }

        final ArrayList<T> result = new ArrayList<>();
        final Map<T, Integer> map1 = countMap(coll1);
        final Map<T, Integer> map2 = countMap(coll2);
        final Set<T> elts = newHashSet(coll2);
        elts.addAll(coll1);
        int m;
        for (T t : elts) {
            m = Math.abs(map1.get(t) - map2.get(t));
            for (int i = 0; i < m; i++) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 判断指定集合是否包含指定值，如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     *
     * @param collection 集合
     * @param value      需要查找的值
     * @return 如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     */
    public static boolean contains(final Collection<?> collection, Object value) {
        return isNotEmpty(collection) && collection.contains(value);
    }

    /**
     * 其中一个集合在另一个集合中是否至少包含一个元素，既是两个集合是否至少有一个共同的元素
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 其中一个集合在另一个集合中是否至少包含一个元素
     * @see #intersection
     */
    public static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        if (isEmpty(coll1) || isEmpty(coll2)) {
            return false;
        }
        if (coll1.size() < coll2.size()) {
            for (Object object : coll1) {
                if (coll2.contains(object)) {
                    return true;
                }
            }
        } else {
            for (Object object : coll2) {
                if (coll1.contains(object)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}<br>
     * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value<br>
     * 例如：[a,b,c,c,c] 得到：<br>
     * a: 1<br>
     * b: 1<br>
     * c: 3<br>
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link Map}
     * @see Iterables#countMap(Iterable)
     */
    public static <T> Map<T, Integer> countMap(Iterable<T> collection) {
        return Iterables.countMap(collection);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @return 连接后的字符串
     * @see Iterables#join(Iterable, CharSequence)
     */
    public static <T> String join(Iterable<T> iterable, CharSequence conjunction) {
        return Iterables.join(iterable, conjunction);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @return 连接后的字符串
     * @see Iterables#join(Iterator, CharSequence)
     */
    public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
        return Iterables.join(iterator, conjunction);
    }

    /**
     * 切取部分数据<br>
     * 切取后的栈将减少这些元素
     *
     * @param <T>             集合元素类型
     * @param surplusAlaDatas 原数据
     * @param partSize        每部分数据的长度
     * @return 切取出的数据或null
     */
    public static <T> List<T> popPart(Stack<T> surplusAlaDatas, int partSize) {
        if (isEmpty(surplusAlaDatas)) {
            return null;
        }

        final List<T> currentAlaDatas = new ArrayList<>();
        int size = surplusAlaDatas.size();
        // 切割
        if (size > partSize) {
            for (int i = 0; i < partSize; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        } else {
            for (int i = 0; i < size; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        }
        return currentAlaDatas;
    }

    /**
     * 切取部分数据<br>
     * 切取后的栈将减少这些元素
     *
     * @param <T>             集合元素类型
     * @param surplusAlaDatas 原数据
     * @param partSize        每部分数据的长度
     * @return 切取出的数据或null
     */
    public static <T> List<T> popPart(Deque<T> surplusAlaDatas, int partSize) {
        if (isEmpty(surplusAlaDatas)) {
            return null;
        }

        final List<T> currentAlaDatas = new ArrayList<>();
        int size = surplusAlaDatas.size();
        // 切割
        if (size > partSize) {
            for (int i = 0; i < partSize; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        } else {
            for (int i = 0; i < size; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        }
        return currentAlaDatas;
    }

    // ----------------------------------------------------------------------------------------------- new HashMap

    /**
     * 新建一个HashMap
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return HashMap对象
     * @see DggMapUtil#newHashMap()
     */
    public static <K, V> Map<K, V> newHashMap() {
        return DggMapUtil.newHashMap();
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>     Key类型
     * @param <V>     Value类型
     * @param size    初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75
     * @param isOrder Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     * @see DggMapUtil#newHashMap(int, boolean)
     */
    public static <K, V> Map<K, V> newHashMap(int size, boolean isOrder) {
        return DggMapUtil.newHashMap(size, isOrder);
    }

    /**
     * 新建一个HashMap
     *
     * @param <K>  Key类型
     * @param <V>  Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75
     * @return HashMap对象
     * @see DggMapUtil#newHashMap(int)
     */
    public static <K, V> Map<K, V> newHashMap(int size) {
        return DggMapUtil.newHashMap(size);
    }

    // ----------------------------------------------------------------------------------------------- new HashSet

    /**
     * 新建一个HashSet
     *
     * @param <T> 集合元素类型
     * @param ts  元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> newHashSet(T... ts) {
        return newHashSet(false, ts);
    }

    /**
     * 新建一个LinkedHashSet
     *
     * @param <T> 集合元素类型
     * @param ts  元素数组
     * @return HashSet对象
     * @since 4.1.10
     */
    @SafeVarargs
    public static <T> LinkedHashSet<T> newLinkedHashSet(T... ts) {
        return (LinkedHashSet<T>) newHashSet(true, ts);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>      集合元素类型
     * @param isSorted 是否有序，有序返回 {@link LinkedHashSet}，否则返回 {@link HashSet}
     * @param ts       元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> newHashSet(boolean isSorted, T... ts) {
        if (null == ts) {
            return isSorted ? new LinkedHashSet<T>() : new HashSet<T>();
        }
        int initialCapacity = Math.max((int) (ts.length / .75f) + 1, 16);
        HashSet<T> set = isSorted ? new LinkedHashSet<T>(initialCapacity) : new HashSet<T>(initialCapacity);
        for (T t : ts) {
            set.add(t);
        }
        return set;
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return HashSet对象
     */
    public static <T> HashSet<T> newHashSet(Collection<T> collection) {
        return newHashSet(false, collection);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>        集合元素类型
     * @param isSorted   是否有序，有序返回 {@link LinkedHashSet}，否则返回{@link HashSet}
     * @param collection 集合，用于初始化Set
     * @return HashSet对象
     */
    public static <T> HashSet<T> newHashSet(boolean isSorted, Collection<T> collection) {
        return isSorted ? new LinkedHashSet<T>(collection) : new HashSet<T>(collection);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>      集合元素类型
     * @param isSorted 是否有序，有序返回 {@link LinkedHashSet}，否则返回{@link HashSet}
     * @param iter     {@link Iterator}
     * @return HashSet对象
     */
    public static <T> HashSet<T> newHashSet(boolean isSorted, Iterator<T> iter) {
        if (null == iter) {
            return newHashSet(isSorted, (T[]) null);
        }
        final HashSet<T> set = isSorted ? new LinkedHashSet<T>() : new HashSet<T>();
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        return set;
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>        集合元素类型
     * @param isSorted   是否有序，有序返回 {@link LinkedHashSet}，否则返回{@link HashSet}
     * @param enumration {@link Enumeration}
     * @return HashSet对象
     */
    public static <T> HashSet<T> newHashSet(boolean isSorted, Enumeration<T> enumration) {
        if (null == enumration) {
            return newHashSet(isSorted, (T[]) null);
        }
        final HashSet<T> set = isSorted ? new LinkedHashSet<T>() : new HashSet<T>();
        while (enumration.hasMoreElements()) {
            set.add(enumration.nextElement());
        }
        return set;
    }

    // ----------------------------------------------------------------------------------------------- List

    /**
     * 新建一个空List
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @return List对象
     */
    public static <T> List<T> list(boolean isLinked) {
        return isLinked ? new LinkedList<T>() : new ArrayList<T>();
    }

    /**
     * 新建一个List
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param values   数组
     * @return List对象
     */
    @SafeVarargs
    public static <T> List<T> list(boolean isLinked, T... values) {
        if (ArrayUtils.isEmpty(values)) {
            return list(isLinked);
        }
        List<T> arrayList = isLinked ? new LinkedList<T>() : new ArrayList<T>(values.length);
        for (T t : values) {
            arrayList.add(t);
        }
        return arrayList;
    }

    /**
     * 新建一个List
     *
     * @param <T>        集合元素类型
     * @param isLinked   是否新建LinkedList
     * @param collection 集合
     * @return List对象
     */
    public static <T> List<T> list(boolean isLinked, Collection<T> collection) {
        if (null == collection) {
            return list(isLinked);
        }
        return isLinked ? new LinkedList<T>(collection) : new ArrayList<T>(collection);
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param iterable {@link Iterable}
     * @return List对象
     */
    public static <T> List<T> list(boolean isLinked, Iterable<T> iterable) {
        if (null == iterable) {
            return list(isLinked);
        }
        return list(isLinked, iterable.iterator());
    }

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param iter     {@link Iterator}
     * @return ArrayList对象
     */
    public static <T> List<T> list(boolean isLinked, Iterator<T> iter) {
        final List<T> list = list(isLinked);
        if (null != iter) {
            while (iter.hasNext()) {
                list.add(iter.next());
            }
        }
        return list;
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>        集合元素类型
     * @param isLinked   是否新建LinkedList
     * @param enumration {@link Enumeration}
     * @return ArrayList对象
     */
    public static <T> List<T> list(boolean isLinked, Enumeration<T> enumration) {
        final List<T> list = list(isLinked);
        if (null != enumration) {
            while (enumration.hasMoreElements()) {
                list.add(enumration.nextElement());
            }
        }
        return list;
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>    集合元素类型
     * @param values 数组
     * @return ArrayList对象
     */
    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(T... values) {
        return (ArrayList<T>) list(false, values);
    }

    /**
     * 数组转为ArrayList
     *
     * @param <T>    集合元素类型
     * @param values 数组
     * @return ArrayList对象
     */
    @SafeVarargs
    public static <T> ArrayList<T> toList(T... values) {
        return newArrayList(values);
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return ArrayList对象
     */
    public static <T> ArrayList<T> newArrayList(Collection<T> collection) {
        return (ArrayList<T>) list(false, collection);
    }

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return ArrayList对象
     */
    public static <T> ArrayList<T> newArrayList(Iterable<T> iterable) {
        return (ArrayList<T>) list(false, iterable);
    }

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>  集合元素类型
     * @param iter {@link Iterator}
     * @return ArrayList对象
     */
    public static <T> ArrayList<T> newArrayList(Iterator<T> iter) {
        return (ArrayList<T>) list(false, iter);
    }

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>        集合元素类型
     * @param enumration {@link Enumeration}
     * @return ArrayList对象
     */
    public static <T> ArrayList<T> newArrayList(Enumeration<T> enumration) {
        return (ArrayList<T>) list(false, enumration);
    }

    // ----------------------------------------------------------------------new LinkedList

    /**
     * 新建LinkedList
     *
     * @param values 数组
     * @param <T>    类型
     * @return LinkedList
     */
    @SafeVarargs
    public static <T> LinkedList<T> newLinkedList(T... values) {
        return (LinkedList<T>) list(true, values);
    }

    /**
     * 新建一个CopyOnWriteArrayList
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link CopyOnWriteArrayList}
     */
    public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(Collection<T> collection) {
        return (null == collection) ? (new CopyOnWriteArrayList<T>()) : (new CopyOnWriteArrayList<T>(collection));
    }

    /**
     * 新建{@link BlockingQueue}<br>
     * 在队列为空时，获取元素的线程会等待队列变为非空。当队列满时，存储元素的线程会等待队列可用。
     *
     * @param capacity 容量
     * @param isLinked 是否为链表形式
     * @return {@link BlockingQueue}
     */
    public static <T> BlockingQueue<T> newBlockingQueue(int capacity, boolean isLinked) {
        BlockingQueue<T> queue;
        if (isLinked) {
            queue = new LinkedBlockingDeque<>(capacity);
        } else {
            queue = new ArrayBlockingQueue<>(capacity);
        }
        return queue;
    }

    /**
     * 创建新的集合对象
     *
     * @param <T>            集合类型
     * @param collectionType 集合类型
     * @return 集合类型对应的实例
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Collection<T> create(Class<?> collectionType) {
        Collection<T> list = null;
        if (collectionType.isAssignableFrom(AbstractCollection.class)) {
            // 抽象集合默认使用ArrayList
            list = new ArrayList<>();
        }

        // Set
        else if (collectionType.isAssignableFrom(HashSet.class)) {
            list = new HashSet<>();
        } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            list = new LinkedHashSet<>();
        } else if (collectionType.isAssignableFrom(TreeSet.class)) {
            list = new TreeSet<>();
        } else if (collectionType.isAssignableFrom(EnumSet.class)) {
            list = (Collection<T>) EnumSet.noneOf((Class<Enum>) DggClassUtil.getTypeArgument(collectionType));
        }

        // List
        else if (collectionType.isAssignableFrom(ArrayList.class)) {
            list = new ArrayList<>();
        } else if (collectionType.isAssignableFrom(LinkedList.class)) {
            list = new LinkedList<>();
        }

        // Others，直接实例化
        else {
            try {
                list = (Collection<T>) DggBeanUtil.newInstance(collectionType);
            } catch (Exception e) {
                throw new DggUtilException(e);
            }
        }
        return list;
    }

    /**
     * 创建Map<br>
     * 传入抽象Map{@link AbstractMap}和{@link Map}类将默认创建{@link HashMap}
     *
     * @param <K>     map键类型
     * @param <V>     map值类型
     * @param mapType map类型
     * @return {@link Map}实例
     * @see DggMapUtil#createMap(Class)
     */
    public static <K, V> Map<K, V> createMap(Class<?> mapType) {
        return DggMapUtil.createMap(mapType);
    }

    /**
     * 去重集合
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link ArrayList}
     */
    public static <T> ArrayList<T> distinct(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        } else if (collection instanceof Set) {
            return new ArrayList<>(collection);
        } else {
            return new ArrayList<>(new LinkedHashSet<>(collection));
        }
    }

    /**
     * 截取集合的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     */
    public static <T> List<T> sub(List<T> list, int start, int end) {
        return sub(list, start, end, 1);
    }

    /**
     * 截取集合的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     */
    public static <T> List<T> sub(List<T> list, int start, int end, int step) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        final int size = list.size();
        if (start < 0) {
            start += size;
        }
        if (end < 0) {
            end += size;
        }
        if (start == size) {
            return new ArrayList<>(0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > size) {
            if (start >= size) {
                return new ArrayList<>(0);
            }
            end = size;
        }

        if (step <= 1) {
            return list.subList(start, end);
        }

        final List<T> result = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * 截取集合的部分
     *
     * @param <T>        集合元素类型
     * @param collection 被截取的数组
     * @param start      开始位置（包含）
     * @param end        结束位置（不包含）
     * @return 截取后的数组，当开始位置超过最大时，返回null
     */
    public static <T> List<T> sub(Collection<T> collection, int start, int end) {
        return sub(collection, start, end, 1);
    }

    /**
     * 截取集合的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空集合
     */
    public static <T> List<T> sub(Collection<T> list, int start, int end, int step) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        return sub(new ArrayList<T>(list), start, end, step);
    }

    /**
     * 对集合按照指定长度分段，每一个段为单独的集合，返回这个集合的列表
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param size       每个段的长度
     * @return 分段列表
     */
    public static <T> List<List<T>> split(Collection<T> collection, int size) {
        final List<List<T>> result = new ArrayList<>();

        ArrayList<T> subList = new ArrayList<>(size);
        for (T t : collection) {
            if (subList.size() >= size) {
                result.add(subList);
                subList = new ArrayList<>(size);
            }
            subList.add(t);
        }
        result.add(subList);
        return result;
    }

    /**
     * 去掉集合中的多个元素
     *
     * @param collection  集合
     * @param elesRemoved 被去掉的元素数组
     * @return 原集合
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> removeAny(Collection<T> collection, T... elesRemoved) {
        collection.removeAll(newHashSet(elesRemoved));
        return collection;
    }

    // ---------------------------------------------------------------------- isEmpty

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Map是否为空
     *
     * @param map 集合
     * @return 是否为空
     * @see DggMapUtil#isEmpty(Map)
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return DggMapUtil.isEmpty(map);
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     * @see Iterables#isEmpty(Iterable)
     */
    public static boolean isEmpty(Iterable<?> iterable) {
        return Iterables.isEmpty(iterable);
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     * @see Iterables#isEmpty(Iterator)
     */
    public static boolean isEmpty(Iterator<?> Iterator) {
        return Iterables.isEmpty(Iterator);
    }

    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isEmpty(Enumeration<?> enumeration) {
        return null == enumeration || false == enumeration.hasMoreElements();
    }

    // ---------------------------------------------------------------------- isNotEmpty

    /**
     * 集合是否为非空
     *
     * @param collection 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return false == isEmpty(collection);
    }

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     * @see DggMapUtil#isNotEmpty(Map)
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return DggMapUtil.isNotEmpty(map);
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     * @see Iterables#isNotEmpty(Iterable)
     */
    public static boolean isNotEmpty(Iterable<?> iterable) {
        return Iterables.isNotEmpty(iterable);
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     * @see Iterables#isNotEmpty(Iterator)
     */
    public static boolean isNotEmpty(Iterator<?> Iterator) {
        return Iterables.isNotEmpty(Iterator);
    }

    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isNotEmpty(Enumeration<?> enumeration) {
        return null != enumeration && enumeration.hasMoreElements();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iterable 被检查的Iterable对象，如果为{@code null} 返回false
     * @return 是否包含{@code null}元素
     * @see Iterables#hasNull(Iterable)
     */
    public static boolean hasNull(Iterable<?> iterable) {
        return Iterables.hasNull(iterable);
    }

    // ---------------------------------------------------------------------- zip

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = a,b,c,d<br>
     * values = 1,2,3,4<br>
     * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param keys      键列表
     * @param values    值列表
     * @param delimiter 分隔符
     * @param isOrder   是否有序
     * @return Map
     */
    public static Map<String, String> zip(String keys, String values, String delimiter, boolean isOrder) {
        return DggArrayUtil.zip(StringUtils.split(keys, delimiter), StringUtils.split(values, delimiter), isOrder);
    }

    /**
     * 映射键值（参考Python的zip()函数），返回Map无序<br>
     * 例如：<br>
     * keys = a,b,c,d<br>
     * values = 1,2,3,4<br>
     * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param keys      键列表
     * @param values    值列表
     * @param delimiter 分隔符
     * @return Map
     */
    public static Map<String, String> zip(String keys, String values, String delimiter) {
        return zip(keys, values, delimiter, false);
    }

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = [a,b,c,d]<br>
     * values = [1,2,3,4]<br>
     * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return Map
     */
    public static <K, V> Map<K, V> zip(Collection<K> keys, Collection<V> values) {
        if (isEmpty(keys) || isEmpty(values)) {
            return null;
        }

        final List<K> keyList = new ArrayList<K>(keys);
        final List<V> valueList = new ArrayList<V>(values);

        final int size = Math.min(keys.size(), values.size());
        final Map<K, V> map = new HashMap<K, V>((int) (size / 0.75));
        for (int i = 0; i < size; i++) {
            map.put(keyList.get(i), valueList.get(i));
        }

        return map;
    }

    /**
     * 将Entry集合转换为HashMap
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param entryIter entry集合
     * @return Map
     * @see Iterables#toMap(Iterable)
     */
    public static <K, V> HashMap<K, V> toMap(Iterable<Entry<K, V>> entryIter) {
        return Iterables.toMap(entryIter);
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
     * Map&lt;Object, Object&gt; colorMap = CollectionUtil.toMap(new String[][] {{
     *     {"RED", "#FF0000"},
     *     {"GREEN", "#00FF00"},
     *     {"BLUE", "#0000FF"}});
     * </pre>
     * <p>
     * 参考：commons-lang
     *
     * @param array 数组。元素类型为Map.Entry、数组、Iterable、Iterator
     * @return {@link HashMap}
     * @see DggMapUtil#of(Object[])
     */
    public static HashMap<Object, Object> toMap(Object[] array) {
        return DggMapUtil.of(array);
    }

    /**
     * 将集合转换为排序后的TreeSet
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param comparator 比较器
     * @return treeSet
     */
    public static <T> TreeSet<T> toTreeSet(Collection<T> collection, Comparator<T> comparator) {
        final TreeSet<T> treeSet = new TreeSet<T>(comparator);
        for (T t : collection) {
            treeSet.add(t);
        }
        return treeSet;
    }

    /**
     * {@link Iterable}转为{@link Collection}<br>
     * 首先尝试强转，强转失败则构建一个新的{@link ArrayList}
     *
     * @param <E>      集合元素类型
     * @param iterable {@link Iterable}
     * @return {@link Collection} 或者 {@link ArrayList}
     * @since 3.0.9
     */
    public static <E> Collection<E> toCollection(Iterable<E> iterable) {
        return (iterable instanceof Collection) ? (Collection<E>) iterable : newArrayList(iterable.iterator());
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
     * @see DggMapUtil#toListMap(Iterable)
     */
    public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
        return DggMapUtil.toListMap(mapList);
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
     * @see DggMapUtil#toMapList(Map)
     */
    public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
        return DggMapUtil.toMapList(listMap);
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param iterator   要加入的{@link Iterator}
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Iterator<T> iterator) {
        if (null != collection && null != iterator) {
            while (iterator.hasNext()) {
                collection.add(iterator.next());
            }
        }
        return collection;
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param iterable   要加入的内容{@link Iterable}
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Iterable<T> iterable) {
        return addAll(collection, iterable.iterator());
    }

    /**
     * 加入全部
     *
     * @param <T>         集合元素类型
     * @param collection  被加入的集合 {@link Collection}
     * @param enumeration 要加入的内容{@link Enumeration}
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, Enumeration<T> enumeration) {
        if (null != collection && null != enumeration) {
            while (enumeration.hasMoreElements()) {
                collection.add(enumeration.nextElement());
            }
        }
        return collection;
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param values     要加入的内容数组
     * @return 原集合
     */
    public static <T> Collection<T> addAll(Collection<T> collection, T[] values) {
        if (null != collection && null != values) {
            for (T value : values) {
                collection.add(value);
            }
        }
        return collection;
    }

    /**
     * 将另一个列表中的元素加入到列表中，如果列表中已经存在此元素则忽略之
     *
     * @param <T>       集合元素类型
     * @param list      列表
     * @param otherList 其它列表
     * @return 此列表
     */
    public static <T> List<T> addAllIfNotContains(List<T> list, List<T> otherList) {
        for (T t : otherList) {
            if (false == list.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 获取集合中指定下标的元素值，下标可以为负数，例如-1表示最后一个元素
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param index      下标，支持负数
     * @return 元素值
     */
    public static <T> T get(Collection<T> collection, int index) {
        if (index < 0) {
            index += collection.size();
        }
        if (collection instanceof List) {
            final List<T> list = ((List<T>) collection);
            return list.get(index);
        } else {
            int i = 0;
            for (T t : collection) {
                if (i > index) {
                    break;
                } else if (i == index) {
                    return t;
                }
                i++;
            }
            //检查越界
            if (index >= i) {
                throw new IndexOutOfBoundsException(MessageFormat.format("Length is {0} but index is {1}", i, index));
            }
        }
        return null;
    }

    /**
     * 获取集合中指定多个下标的元素值，下标可以为负数，例如-1表示最后一个元素
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param indexes    下标，支持负数
     * @return 元素值列表
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getAny(Collection<T> collection, int... indexes) {
        final int size = collection.size();
        final ArrayList<T> result = new ArrayList<>();
        if (collection instanceof List) {
            final List<T> list = ((List<T>) collection);
            for (int index : indexes) {
                if (index < 0) {
                    index += size;
                }
                result.add(list.get(index));
            }
        } else {
            Object[] array = ((Collection<T>) collection).toArray();
            for (int index : indexes) {
                if (index < 0) {
                    index += size;
                }
                result.add((T) array[index]);
            }
        }
        return result;
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return 第一个元素
     * @see Iterables#getFirst(Iterable)
     */
    public static <T> T getFirst(Iterable<T> iterable) {
        return Iterables.getFirst(iterable);
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个元素
     * @see Iterables#getFirst(Iterator)
     */
    public static <T> T getFirst(Iterator<T> iterator) {
        return Iterables.getFirst(iterator);
    }

    /**
     * 获取集合的最后一个元素
     *
     * @param <T>        集合元素类型
     * @param collection {@link Collection}
     * @return 最后一个元素
     */
    public static <T> T getLast(Collection<T> collection) {
        return get(collection, -1);
    }

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, K... keys) {
        final ArrayList<V> values = new ArrayList<>();
        for (K k : keys) {
            values.add(map.get(k));
        }
        return values;
    }

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     */
    public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterable<K> keys) {
        return valuesOfKeys(map, keys.iterator());
    }

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     */
    public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterator<K> keys) {
        final ArrayList<V> values = new ArrayList<>();
        while (keys.hasNext()) {
            values.add(map.get(keys.next()));
        }
        return values;
    }

    // ------------------------------------------------------------------------------------------------- sort

    /**
     * 排序集合，排序不会修改原集合
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param comparator 比较器
     * @return treeSet
     */
    public static <T> List<T> sort(Collection<T> collection, Comparator<? super T> comparator) {
        List<T> list = new ArrayList<T>(collection);
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * 针对List排序，排序会修改原List
     *
     * @param <T>  元素类型
     * @param list 被排序的List
     * @param c    {@link Comparator}
     * @return 原list
     * @see Collections#sort(List, Comparator)
     */
    public static <T> List<T> sort(List<T> list, Comparator<? super T> c) {
        Collections.sort(list, c);
        return list;
    }

    /**
     * 排序Map
     *
     * @param <K>        键类型
     * @param <V>        值类型
     * @param map        Map
     * @param comparator Entry比较器
     * @return {@link TreeMap}
     */
    public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
        final TreeMap<K, V> result = new TreeMap<K, V>(comparator);
        result.putAll(map);
        return result;
    }

    /**
     * 通过Entry排序，可以按照键排序，也可以按照值排序，亦或者两者综合排序
     *
     * @param <K>             键类型
     * @param <V>             值类型
     * @param entryCollection Entry集合
     * @param comparator      {@link Comparator}
     * @return {@link LinkedList}
     */
    public static <K, V> LinkedHashMap<K, V> sortToMap(Collection<Entry<K, V>> entryCollection, Comparator<Entry<K, V>> comparator) {
        List<Entry<K, V>> list = new LinkedList<>(entryCollection);
        Collections.sort(list, comparator);

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 通过Entry排序，可以按照键排序，也可以按照值排序，亦或者两者综合排序
     *
     * @param <K>        键类型
     * @param <V>        值类型
     * @param map        被排序的Map
     * @param comparator {@link Comparator}
     * @return {@link LinkedList}
     */
    public static <K, V> LinkedHashMap<K, V> sortByEntry(Map<K, V> map, Comparator<Entry<K, V>> comparator) {
        return sortToMap(map.entrySet(), comparator);
    }

    /**
     * 循环遍历 {@link Iterator}，使用{@link Consumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @param consumer {@link Consumer} 遍历的每条数据处理器
     */
    public static <T> void forEach(Iterator<T> iterator, Consumer<T> consumer) {
        int index = 0;
        while (iterator.hasNext()) {
            consumer.accept(iterator.next(), index);
            index++;
        }
    }

    /**
     * 循环遍历 {@link Enumeration}，使用{@link Consumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param <T>         集合元素类型
     * @param enumeration {@link Enumeration}
     * @param consumer    {@link Consumer} 遍历的每条数据处理器
     */
    public static <T> void forEach(Enumeration<T> enumeration, Consumer<T> consumer) {
        int index = 0;
        while (enumeration.hasMoreElements()) {
            consumer.accept(enumeration.nextElement(), index);
            index++;
        }
    }

    /**
     * 循环遍历Map，使用{@link KVConsumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param <K>        Key类型
     * @param <V>        Value类型
     * @param map        {@link Map}
     * @param kvConsumer {@link KVConsumer} 遍历的每条数据处理器
     */
    public static <K, V> void forEach(Map<K, V> map, KVConsumer<K, V> kvConsumer) {
        int index = 0;
        for (Entry<K, V> entry : map.entrySet()) {
            kvConsumer.accept(entry.getKey(), entry.getValue(), index);
            index++;
        }
    }

    /**
     * 反序给定List，会在原List基础上直接修改
     *
     * @param <T>  元素类型
     * @param list 被反转的List
     * @return 反转后的List
     * @since 4.0.6
     */
    public static <T> List<T> reverse(List<T> list) {
        Collections.reverse(list);
        return list;
    }

    /**
     * 反序给定List，会创建一个新的List，原List数据不变
     *
     * @param <T>  元素类型
     * @param list 被反转的List
     * @return 反转后的List
     */
    public static <T> List<T> reverseNew(List<T> list) {
        final List<T> list2 = ObjectUtils.clone(list);
        return reverse(list2);
    }

    /**
     * 设置或增加元素。当index小于List的长度时，替换指定位置的值，否则在尾部追加
     *
     * @param list    List列表
     * @param index   位置
     * @param element 新元素
     * @return 原List
     * @since 4.1.2
     */
    public static <T> List<T> setOrAppend(List<T> list, int index, T element) {
        if (index < list.size()) {
            list.set(index, element);
        } else {
            list.add(element);
        }
        return list;
    }

    // ---------------------------------------------------------------------------------------------- Interface start

    /**
     * 针对一个参数做相应的操作
     *
     * @param <T> 处理参数类型
     * @author Looly
     */
    public static interface Consumer<T> {
        /**
         * 接受并处理一个参数
         *
         * @param value 参数值
         * @param index 参数在集合中的索引
         */
        void accept(T value, int index);
    }

    /**
     * 针对两个参数做相应的操作，例如Map中的KEY和VALUE
     *
     * @param <K> KEY类型
     * @param <V> VALUE类型
     * @author Looly
     */
    public static interface KVConsumer<K, V> {
        /**
         * 接受并处理一对参数
         *
         * @param key   键
         * @param value 值
         * @param index 参数在集合中的索引
         */
        void accept(K key, V value, int index);
    }

    /**
     * Hash计算接口
     *
     * @param <T> 被计算hash的对象类型
     * @author looly
     * @since 3.2.2
     */
    public static interface Hash<T> {
        /**
         * 计算Hash值
         *
         * @param t 对象
         * @return hash
         */
        int hash(T t);
    }
    // ---------------------------------------------------------------------------------------------- Interface end
}
