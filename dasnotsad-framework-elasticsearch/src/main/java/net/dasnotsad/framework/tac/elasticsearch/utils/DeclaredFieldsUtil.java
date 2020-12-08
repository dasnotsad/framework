package net.dasnotsad.framework.tac.elasticsearch.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 统一反射方法
 *
 * @author liuliwei
 * @create 2019-8-26
 */
public class DeclaredFieldsUtil {

    //注解缓存
    private static Map<Integer, Field> annotationMap = new HashMap<>();

    /**
     * 找到指定类中包含指定注解的field
     *
     * @param clazz           目标类
     * @param annotationClass 指定的注解
     * @return Field 包含指定注解的field
     */
    public static Field findAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Integer mapKey = (clazz.getName().concat(annotationClass.getName())).hashCode();
        if (annotationMap.containsKey(mapKey))
            return annotationMap.get(mapKey);
        Field resField = null;
        List<Field> fieldList = new ArrayList<>();
        Class<?> tempClass = clazz;
        while (tempClass != null) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        loop:
        for (Field field : fieldList)
            if (field.isAnnotationPresent(annotationClass)) {
                resField = field;
                break loop;
            }
        if (resField != null) {
            resField.setAccessible(true);
            annotationMap.put(mapKey, resField);
        }
        return resField;
    }
}