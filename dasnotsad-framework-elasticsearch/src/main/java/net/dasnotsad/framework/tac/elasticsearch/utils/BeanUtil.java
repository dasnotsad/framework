package net.dasnotsad.framework.tac.elasticsearch.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.lang.reflect.*;
import java.util.*;

/**
 * @Author: wangxianwei
 * @Date: 2020/12/2
 * @Description: Class类解析工具
 */
public final class BeanUtil {

    /**
     * 深度获取类属性和属性类型（类树形结构）
     *
     * @param clazz 类
     * @return 类树形结构
     */
    public static Node treeNode(Class clazz) {
        return Node.builder().nodes(classToFieldTree(clazz)).build();
    }

    private static Set<Node> classToFieldTree(Class clazz) {
        // 父类Field处理
        Field[] allFields = null;
        Class<?> searchType = clazz;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                allFields = ArrayUtil.append(allFields, declaredFields);
            }
            searchType = searchType.getSuperclass();
        }

        // node递归处理
        Set<Node> set = new HashSet<>();
        Arrays.stream(Objects.requireNonNull(allFields)).forEach(field -> {
            Class fClass = field.getType();
            Node subNode = Node.builder().field(field).build();
            if (Collection.class.isAssignableFrom(fClass)) {
                Type gt = field.getGenericType();
                if (gt instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) gt;
                    Class generic = (Class) pt.getActualTypeArguments()[0];
                    subNode.setGeneric(pt.getActualTypeArguments());
                    if (!generic.isPrimitive() && !generic.getName().startsWith("java.")) {
                        subNode.setNodes(classToFieldTree(generic));
                    }
                }
            } else if (Map.class.isAssignableFrom(fClass)) {
                Type gt = field.getGenericType();
                if (gt instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) gt;
                    Class generic = (Class) pt.getActualTypeArguments()[1];
                    subNode.setGeneric(pt.getActualTypeArguments());
                    if (!generic.isPrimitive() && !generic.getName().startsWith("java.")) {
                        subNode.setNodes(classToFieldTree(generic));
                    }
                }
            } else if (!fClass.isPrimitive() && !fClass.getName().startsWith("java.")) {
                subNode.setNodes(classToFieldTree(fClass));
            }
            set.add(subNode);
        });
        return set;
    }

    /**
     * 类树形结构
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Node {
        private Field field;
        private Type[] generic;
        private Set<Node> nodes;
    }
}
