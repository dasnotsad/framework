package net.dgg.utils.core;

import net.dgg.utils.common.SimpleCache;
import net.dgg.utils.exception.DggAsserts;
import net.dgg.utils.exception.DggUtilException;
import net.dgg.utils.string.DggStringUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * @Description: 反射工具类
 * @Author Created by yan.x on 2019-04-05 .
 * @Copyright © HOT SUN group.All Rights Reserved.
 **/
public final class DggReflectUtil {

    /**
     * 构造对象缓存
     */
    private static final SimpleCache<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new SimpleCache<>();
    /**
     * 字段缓存
     */
    private static final SimpleCache<Class<?>, Field[]> FIELDS_CACHE = new SimpleCache<>();
    /**
     * 方法缓存
     */
    private static final SimpleCache<Class<?>, Method[]> METHODS_CACHE = new SimpleCache<>();

    //---------------------------------------------------------------------------------------------- newInstance start

    /**
     * 实例化对象
     *
     * @param <T>    对象类型
     * @param clazz  类
     * @param params 构造函数参数
     * @return 对象
     * @throws DggUtilException 包装各类异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, Object... params) throws DggUtilException {
        if (ArrayUtils.isEmpty(params)) {
            if (Map.class.isAssignableFrom(clazz)) {
                // Map
                if (LinkedHashMap.class.isAssignableFrom(clazz)) {
                    return (T) new LinkedHashMap(16);
                } else {
                    return (T) new HashMap(16);
                }
            } else if (Iterable.class.isAssignableFrom(clazz)) {
                // Iterable
                if (LinkedHashSet.class.isAssignableFrom(clazz)) {
                    return (T) new LinkedHashSet<>();
                } else if (Set.class.isAssignableFrom(clazz)) {
                    return (T) new HashSet<>();
                } else if (LinkedList.class.isAssignableFrom(clazz)) {
                    return (T) new LinkedList<>();
                } else {
                    return (T) new ArrayList<T>();
                }
            }

            final Constructor<T> constructor = getConstructor(clazz);
            try {
                return constructor.newInstance();
            } catch (Exception e) {
                throw new DggUtilException(e, "Instance class [{0}] error!", clazz);
            }
        }

        final Class<?>[] paramTypes = DggClassUtil.getClasses(params);
        final Constructor<T> constructor = getConstructor(clazz, paramTypes);
        if (null == constructor) {
            throw new DggUtilException(MessageFormat.format("No Constructor matched for parameter types: [{0}]", paramTypes));
        }
        try {
            return constructor.newInstance(params);
        } catch (Exception e) {
            throw new DggUtilException(e, "Instance class [{0}] error!", clazz);
        }
    }

    //---------------------------------------------------------------------------------------------- newInstance end

    // --------------------------------------------------------------------------------------------------------- Constructor

    /**
     * 查找类中的指定参数的构造方法
     *
     * @param <T>            对象类型
     * @param clazz          类
     * @param parameterTypes 参数类型，只要任何一个参数是指定参数的父类或接口或相等即可
     * @return 构造方法，如果未找到返回null
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        if (null == clazz) {
            return null;
        }

        final Constructor<?>[] constructors = clazz.getConstructors();
        Class<?>[] pts;
        for (Constructor<?> constructor : constructors) {
            pts = constructor.getParameterTypes();
            if (DggClassUtil.isAllAssignableFrom(pts, parameterTypes)) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    /**
     * 获得一个类中所有构造列表
     *
     * @param <T>       构造的对象类型
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T>[] getConstructors(Class<T> beanClass) throws SecurityException {
        DggAsserts.isNotNull(beanClass);
        Constructor<?>[] constructors = CONSTRUCTORS_CACHE.get(beanClass);
        if (null != constructors) {
            return (Constructor<T>[]) constructors;
        }

        constructors = getConstructorsDirectly(beanClass);
        return (Constructor<T>[]) CONSTRUCTORS_CACHE.put(beanClass, constructors);
    }

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Constructor<?>[] getConstructorsDirectly(Class<?> beanClass) throws SecurityException {
        DggAsserts.isNotNull(beanClass);
        return beanClass.getDeclaredConstructors();
    }

    // --------------------------------------------------------------------------------------------------------- GenricType

    /**
     * 通过反射获取定义Class时声明的父类的范型参数的类型.</p>
     * 如: public BookManager extends GenricManager<Book>
     *
     * @param clazz :  需要获取泛型的类Class
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static Class getSuperClassGenricType(Class clazz) throws IndexOutOfBoundsException {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射获取定义Class时声明的父类的范型参数的类型.</p>
     * 如: public BookManager extends GenricManager<Book>
     *
     * @param clazz :  需要获取泛型的类Class
     * @param index :  泛型索引,默认第1个
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static Class getSuperClassGenricType(Class clazz, int index) throws IndexOutOfBoundsException {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * 通过反射获取定义Class时声明的接口的范型参数的类型.</p>
     * 如: public BookManager extends GenricManager<Book>
     *
     * @param clazz :  需要获取泛型的类Class
     * @return
     * @throws IndexOutOfBoundsException
     */
    public static Class getInterfacesGenricType(Class clazz) throws IndexOutOfBoundsException {
        Type[] types = clazz.getGenericInterfaces();
        if (types.length < 0) {
            return Object.class;
        }
        ParameterizedType parameterized = (ParameterizedType) types[0];
        return (Class) parameterized.getActualTypeArguments()[0];
    }


    // --------------------------------------------------------------------------------------------------------- Field

    /**
     * 查找指定类中的所有字段（包括非public字段），也包括父类和Object类的字段， 字段不存在则返回<code>null</code>
     *
     * @param beanClass 被查找字段的类,不能为null
     * @param name      字段名
     * @return 字段
     * @throws SecurityException 安全异常
     */
    public static Field getField(Class<?> beanClass, String name) throws SecurityException {
        final Field[] fields = getFields(beanClass);
        if (ArrayUtils.isNotEmpty(fields)) {
            for (Field field : fields) {
                if ((name.equals(field.getName()))) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFields(Class<?> beanClass) throws SecurityException {
        Field[] allFields = FIELDS_CACHE.get(beanClass);
        if (null != allFields) {
            return allFields;
        }

        allFields = getFieldsDirectly(beanClass, true);
        return FIELDS_CACHE.put(beanClass, allFields);
    }

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存
     *
     * @param beanClass           类
     * @param withSuperClassFieds 是否包括父类的字段列表
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFieds) throws SecurityException {
        DggAsserts.isNotNull(beanClass);

        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                allFields = DggArrayUtil.append(allFields, declaredFields);
            }
            searchType = withSuperClassFieds ? searchType.getSuperclass() : null;
        }

        return allFields;
    }

    /**
     * 获取字段值
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @return 字段值
     * @throws DggUtilException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(Object obj, String fieldName) throws DggUtilException {
        if (null == obj || StringUtils.isBlank(fieldName)) {
            return null;
        }
        return getFieldValue(obj, getField(obj.getClass(), fieldName));
    }

    /**
     * 获取字段值
     *
     * @param obj   对象
     * @param field 字段
     * @return 字段值
     * @throws DggUtilException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(Object obj, Field field) throws DggUtilException {
        if (null == obj || null == field) {
            return null;
        }
        field.setAccessible(true);
        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            throw new DggUtilException(e, "IllegalAccess for {}.{}", obj.getClass(), field.getName());
        }
        return result;
    }

    /**
     * 设置字段值
     *
     * @param obj       对象
     * @param fieldName 字段名
     * @param value     值，值类型必须与字段类型匹配，不会自动转换对象类型
     * @throws DggUtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws DggUtilException {
        DggAsserts.isNotNull(obj);
        DggAsserts.isNotBlank(fieldName);
        setFieldValue(obj, getField(obj.getClass(), fieldName), value);
    }

    /**
     * 设置字段值
     *
     * @param obj   对象
     * @param field 字段
     * @param value 值，值类型必须与字段类型匹配，不会自动转换对象类型
     * @throws DggUtilException DggUtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue(Object obj, Field field, Object value) throws DggUtilException {
        DggAsserts.isNotNull(obj);
        DggAsserts.isNotNull(field);
        try {
            if (field.isAccessible()) {
                field.set(obj, value);
            } else {
                field.setAccessible(true);
                field.set(obj, value);
                field.setAccessible(false);
            }
        } catch (IllegalAccessException e) {
            throw new DggUtilException(e, "IllegalAccess for {}.{}", obj.getClass(), field.getName());
        }
    }


    // --------------------------------------------------------------------------------------------------------- method

    /**
     * 反射 method 方法名，例如 getId
     *
     * @param field : 字段属性
     * @param str   : 属性字符串内容
     * @return
     */
    public static String getMethodCapitalize(Field field, final String str) {
        Class<?> fieldType = field.getType();
        return DggStringUtil.concatCapitalize(boolean.class.equals(fieldType) ? "is" : "get", str);
    }


    /**
     * 获得一个类中所有方法列表，包括其父类中的方法
     *
     * @param beanClass : 类
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethods(Class<?> beanClass) throws SecurityException {
        Method[] allMethods = METHODS_CACHE.get(beanClass);
        if (null != allMethods) {
            return allMethods;
        }

        allMethods = getMethodsDirectly(beanClass, true);
        return METHODS_CACHE.put(beanClass, allMethods);
    }

    /**
     * 获得一个类中所有方法列表，直接反射获取，无缓存
     *
     * @param beanClass             : 类
     * @param withSuperClassMethods : 是否包括父类的方法列表
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSuperClassMethods) throws SecurityException {
        DggAsserts.isNotNull(beanClass);

        Method[] allMethods = null;
        Class<?> searchType = beanClass;
        Method[] declaredMethods;
        while (searchType != null) {
            declaredMethods = searchType.getDeclaredMethods();
            if (null == allMethods) {
                allMethods = declaredMethods;
            } else {
                allMethods = DggArrayUtil.append(allMethods, declaredMethods);
            }
            searchType = withSuperClassMethods ? searchType.getSuperclass() : null;
        }

        return allMethods;
    }

    /**
     * 查找指定对象中的所有方法（包括非public方法），也包括父对象和Object类的方法
     *
     * @param obj        : 被查找的对象，如果为{@code null}返回{@code null}
     * @param methodName : 方法名，如果为空字符串返回{@code null}
     * @param args       : 参数
     * @return 方法
     * @throws SecurityException 无访问权限抛出异常
     */
    public static Method getMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
        if (null == obj || StringUtils.isBlank(methodName)) {
            return null;
        }
        return getMethod(obj.getClass(), methodName, DggClassUtil.getClasses(args));
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回<code>null</code>
     *
     * @param clazz      : 类，如果为{@code null}返回{@code null}
     * @param methodName : 方法名，如果为空字符串返回{@code null}
     * @param paramTypes : 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        return getMethod(clazz, false, methodName, paramTypes);
    }

    /**
     * 查找指定方法 如果找不到对应的方法则返回<code>null</code>
     *
     * @param clazz      : 类，如果为{@code null}返回{@code null}
     * @param ignoreCase : 是否忽略大小写
     * @param methodName : 方法名，如果为空字符串返回{@code null}
     * @param paramTypes : 参数类型，指定参数类型如果是方法的子类也算
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getMethod(Class<?> clazz, boolean ignoreCase, String methodName, Class<?>... paramTypes) throws SecurityException {
        if (null == clazz || StringUtils.isBlank(methodName)) {
            return null;
        }

        final Method[] methods = getMethods(clazz);
        if (ArrayUtils.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (DggStringUtil.equals(methodName, method.getName(), ignoreCase)) {
                    if (ArrayUtils.isEmpty(paramTypes) || DggClassUtil.isAllAssignableFrom(method.getParameterTypes(), paramTypes)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    // --------------------------------------------------------------------------------------------------------- invoke

    /**
     * 执行静态方法
     *
     * @param beanClass  : 类，如果为{@code null}返回{@code null}
     * @param methodName : 方法名，如果为空字符串返回{@code null}
     * @param args       : 参数对象
     * @param <T>        : 对象类型
     * @return 执行结果
     * @throws DggUtilException
     */
    public static <T> T invokeStatic(Class<?> beanClass, String methodName, Object... args) throws DggUtilException {
        final Method method = getMethodOfObj(beanClass, methodName, args);
        if (null == method) {
            throw new DggUtilException(DggStringUtil.format("No such method: [{}]", methodName));
        }
        return invoke(null, method, args);
    }

    /**
     * 执行静态方法
     *
     * @param <T>    : 对象类型
     * @param method : 方法（对象方法或static方法都可）
     * @param args   : 参数对象
     * @return 执行结果
     * @throws DggUtilException 多种异常包装
     */
    public static <T> T invokeStatic(Method method, Object... args) throws DggUtilException {
        return invoke(null, method, args);
    }

    /**
     * 执行方法,执行前要检查给定参数<br>
     * <pre>
     * 1. 参数个数是否与方法参数个数一致
     * 2. 如果某个参数为null但是方法这个位置的参数为原始类型，则赋予原始类型默认值
     * </pre>
     *
     * @param <T>    : 返回对象类型
     * @param obj    : 对象，如果执行静态方法，此值为<code>null</code>
     * @param method : 方法（对象方法或static方法都可）
     * @param args   : 参数对象
     * @return 执行结果
     * @throws DggUtilException 一些列异常的包装
     */
    public static <T> T invokeWithCheck(Object obj, Method method, Object... args) throws DggUtilException {
        final Class<?>[] types = method.getParameterTypes();
        if (null != types && null != args) {
            DggAsserts.isTrue(args.length == types.length, "Params length [{}] is not fit for param length [{}] of method !", args.length, types.length);
            Class<?> type;
            for (int i = 0; i < args.length; i++) {
                type = types[i];
                if (type.isPrimitive() && null == args[i]) {
                    // 参数是原始类型，而传入参数为null时赋予默认值
                    args[i] = DggClassUtil.getDefaultValue(type);
                }
            }
        }

        return invoke(obj, method, args);
    }

    /**
     * 执行方法
     *
     * @param <T>    : 返回对象类型
     * @param obj    : 对象，如果执行静态方法，此值为<code>null</code>
     * @param method : 方法（对象方法或static方法都可）
     * @param args   : 参数对象
     * @return 执行结果
     * @throws DggUtilException 一些列异常的包装
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object obj, Method method, Object... args) throws DggUtilException {
        if (false == method.isAccessible()) {
            method.setAccessible(true);
        }
        try {
            return (T) method.invoke(DggClassUtil.isStatic(method) ? null : obj, args);
        } catch (Exception e) {
            throw new DggUtilException(e);
        }
    }

    /**
     * 执行对象中指定方法
     *
     * @param <T>        : 返回对象类型
     * @param obj        : 方法所在对象
     * @param methodName : 方法名
     * @param args       : 参数列表
     * @return 执行结果
     * @throws DggUtilException IllegalAccessException包装
     */
    public static <T> T invoke(Object obj, String methodName, Object... args) throws DggUtilException {
        final Method method = getMethodOfObj(obj, methodName, args);
        if (null == method) {
            throw new DggUtilException(MessageFormat.format("No such method: [{0}]", methodName));
        }
        return invoke(obj, method, args);
    }

    /**
     * 执行对象中指定方法
     *
     * @param <T>        : 返回对象类型
     * @param clazz      : 方法所在对象
     * @param methodName : 方法名
     * @param args       : 参数列表
     * @return 执行结果
     */
    public static <T> T invoke(Class<T> clazz, String methodName, Object... args) throws DggUtilException {
        if (null == clazz || StringUtils.isEmpty(methodName)) {
            return null;
        }
        final Method method = getMethod(clazz, methodName, DggClassUtil.getClasses(args));
        if (null == method) {
            throw new DggUtilException(String.format("No such method: [%s]", methodName));
        }
        return invoke(DggSingletonUtil.get(clazz), method, args);
    }
}
