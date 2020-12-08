package net.dgg.utils.core;


import net.dgg.utils.constant.BasicType;
import net.dgg.utils.exception.DggUtilException;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 类工具
 * @Version 1.0.0
 * @Author Created by yan.x on 2019-04-29 .
 * @Copyright © dgg group.All Rights Reserved.
 **/
public final class DggClassUtil extends org.apache.commons.lang3.ClassUtils {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP = new HashMap<>(9);

    static {
        PRIMITIVE_MAP.put(String.class, String.class);
        PRIMITIVE_MAP.put(Boolean.class, boolean.class);
        PRIMITIVE_MAP.put(Byte.class, byte.class);
        PRIMITIVE_MAP.put(Character.class, char.class);
        PRIMITIVE_MAP.put(Double.class, double.class);
        PRIMITIVE_MAP.put(Float.class, float.class);
        PRIMITIVE_MAP.put(Integer.class, int.class);
        PRIMITIVE_MAP.put(Long.class, long.class);
        PRIMITIVE_MAP.put(Short.class, short.class);
        PRIMITIVE_MAP.put(Date.class, Date.class);
    }

    /**
     * @param clazz
     * @return boolean
     * @description 判断基本类型
     * @see Boolean#TYPE
     * @see Character#TYPE
     * @see Byte#TYPE
     * @see Short#TYPE
     * @see Integer#TYPE
     * @see Long#TYPE
     * @see Float#TYPE
     * @see Double#TYPE
     * @see Boolean#TYPE
     * @see char
     * @see byte
     * @see short
     * @see int
     * @see long
     * @see float
     * @see double
     */
    public static boolean isPrimitive(Class<?> clazz) {
        if (PRIMITIVE_MAP.containsKey(clazz)) {
            return true;
        }
        return clazz.isPrimitive();
    }

    /**
     * 是否为基本类型（包括包装类和原始类）
     *
     * @param clazz : 类
     * @return 是否为基本类型
     */
    public static boolean isBasicType(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * 比较判断types1和types2两组类，如果types1中所有的类都与types2对应位置的类相同，或者是其父类或接口，则返回<code>true</code>
     *
     * @param types1 : 类组1
     * @param types2 : 类组2
     * @return 是否相同、父类或接口
     */
    public static boolean isAllAssignableFrom(Class<?>[] types1, Class<?>[] types2) {
        if (ArrayUtils.isEmpty(types1) && ArrayUtils.isEmpty(types2)) {
            return true;
        }
        if (types1.length != types2.length) {
            return false;
        }

        Class<?> type1;
        Class<?> type2;
        for (int i = 0; i < types1.length; i++) {
            type1 = types1[i];
            type2 = types2[i];
            if (isBasicType(type1) && isBasicType(type2)) {
                //原始类型和包装类型存在不一致情况
                if (BasicType.unWrap(type1) != BasicType.unWrap(type2)) {
                    return false;
                }
            } else if (false == type1.isAssignableFrom(type2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为标准的类<br>
     * 这个类必须：
     * <pre>
     * 1、非接口
     * 2、非抽象类
     * 3、非Enum枚举
     * 4、非数组
     * 5、非注解
     * 6、非原始类型（int, long等）
     * </pre>
     *
     * @param clazz 类
     * @return 是否为标准类
     */
    public static boolean isNormalClass(Class<?> clazz) {
        return null != clazz //
                && false == clazz.isInterface() //
                && false == isAbstract(clazz) //
                && false == clazz.isEnum() //
                && false == clazz.isArray() //
                && false == clazz.isAnnotation() //
                && false == clazz.isSynthetic() //
                && false == clazz.isPrimitive();//
    }


    /**
     * 是否为静态方法
     *
     * @param method : 方法
     * @return 是否为静态方法
     */
    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * 是否为抽象类
     *
     * @param clazz 类
     * @return 是否为抽象类
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * @param tartget
     * @param fieldName
     * @return Class<?>
     * @description 获取方法返回值类型
     */
    public static Class<?> getElementType(Class<?> tartget, String fieldName) {
        Class<?> elementTypeClass = null;
        try {
            Type type = tartget.getDeclaredField(fieldName).getGenericType();
            ParameterizedType t = (ParameterizedType) type;
            String classStr = t.getActualTypeArguments()[0].toString().replace("class ", "");
            elementTypeClass = Thread.currentThread().getContextClassLoader().loadClass(classStr);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
            throw new DggUtilException("get fieldName[" + fieldName + "] error", e);
        }
        return elementTypeClass;
    }

    /**
     * 获取指定类型分的默认值<br>
     * 默认值规则为：
     * <pre>
     * 1、如果为原始类型，返回0
     * 2、非原始类型返回{@code null}
     * </pre>
     *
     * @param clazz 类
     * @return 默认值
     */
    public static Object getDefaultValue(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (long.class == clazz) {
                return 0L;
            } else if (int.class == clazz) {
                return 0;
            } else if (short.class == clazz) {
                return (short) 0;
            } else if (char.class == clazz) {
                return (char) 0;
            } else if (byte.class == clazz) {
                return (byte) 0;
            } else if (double.class == clazz) {
                return 0D;
            } else if (float.class == clazz) {
                return 0f;
            } else if (boolean.class == clazz) {
                return false;
            }
        }
        return null;
    }

    /**
     * 获得对象数组的类数组
     *
     * @param objects 对象数组，如果数组中存在{@code null}元素，则此元素被认为是Object类型
     * @return 类数组
     */
    public static Class<?>[] getClasses(Object... objects) {
        Class<?>[] classes = new Class<?>[objects.length];
        Object obj;
        for (int i = 0; i < objects.length; i++) {
            obj = objects[i];
            classes[i] = (null == obj) ? Object.class : obj.getClass();
        }
        return classes;
    }

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param clazz 被检查的类，必须是已经确定泛型类型的类
     * @return {@link Class}
     */
    public static Class<?> getTypeArgument(Class<?> clazz) {
        return getTypeArgument(clazz, 0);
    }

    /**
     * 获得给定类的泛型参数
     *
     * @param clazz 被检查的类，必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号，既第几个泛型类型
     * @return {@link Class}
     */
    public static Class<?> getTypeArgument(Class<?> clazz, int index) {
        final Type argumentType = DggTypeUtil.getTypeArgument(clazz, index);
        if (null != argumentType && argumentType instanceof Class) {
            return (Class<?>) argumentType;
        }
        return null;
    }

    /**
     * <p>
     * 请仅在确定类存在的情况下调用该方法
     * </p>
     *
     * @param name 类名称
     * @return 返回转换后的 Class
     */
    public static Class<?> toClassConfident(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new DggUtilException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法", e);
        }
    }
}