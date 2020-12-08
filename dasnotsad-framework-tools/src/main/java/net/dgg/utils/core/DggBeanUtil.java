package net.dgg.utils.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dgg.utils.exception.DggUtilException;
import net.dgg.utils.string.DggStringUtil;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.core.Converter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * describe: 实体工具类，目前copy不支持map、list
 * Created by yan.x on 2018/3/13 18:23.
 **/
public final class DggBeanUtil extends org.springframework.beans.BeanUtils {

    private static final Map<String, BeanCopier> BEAN_COPIERS = new ConcurrentHashMap<>();

    private DggBeanUtil() {
    }

    /**
     * 实例化对象
     *
     * @param clazz 类
     * @return 对象
     */
    public static <T> T newInstance(Class<?> clazz) {
        return (T) instantiateClass(clazz);
    }

    /**
     * 实例化对象
     *
     * @param clazzStr 类名
     * @return 对象
     */
    public static <T> T newInstance(String clazzStr) {
        try {
            Class<?> clazz = Class.forName(clazzStr);
            return newInstance(clazz);
        } catch (ClassNotFoundException e) {
            throw new DggUtilException(e);
        }
    }

    /**
     * 获取对象的属性
     *
     * @param obj
     * @param field
     * @return
     */
    public static Object getFieldValue(Object obj, String field) {
        if (field == null) {
            return null;
        }
        String f = field.substring(0, 1);
        String methodName = "get" + f.toUpperCase() + field.substring(1);
        try {
            Method method = obj.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取对象的属性（有json对象的数据）
     *
     * @param obj
     * @param field
     * @return
     */
    public static Object getFieldJson(Object obj, String field) {
        if (field == null) {
            return null;
        }
        try {
            String f = field.substring(0, 1);
            String methodName = "";
            if (field.indexOf(".") > 0) {
                String jStr = field.substring(field.indexOf(".") + 1, field.length());
                methodName = "get" + f.toUpperCase() + field.substring(1, field.indexOf("."));
                Method method = obj.getClass().getMethod(methodName);
                method.setAccessible(true);
                Object object = method.invoke(obj);
                if (object != null && object instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    return jsonObject.get(jStr);
                } else {
                    return null;
                }
            } else {
                methodName = "get" + f.toUpperCase() + field.substring(1);
                Method method = obj.getClass().getMethod(methodName);
                method.setAccessible(true);
                return method.invoke(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Bean的属性: 调用get方法
     *
     * @param bean         bean
     * @param propertyName 属性名
     * @return 属性值
     */
    public static Object getProperty(Object bean, String propertyName) {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd == null) {
            throw new DggUtilException("Could not read property '" + propertyName + "' from bean PropertyDescriptor is null");
        }
        Method readMethod = pd.getReadMethod();
        if (readMethod == null) {
            throw new DggUtilException("Could not read property '" + propertyName + "' from bean readMethod is null");
        }
        if (!readMethod.isAccessible()) {
            readMethod.setAccessible(true);
        }
        try {
            return readMethod.invoke(bean);
        } catch (Throwable ex) {
            throw new DggUtilException("Could not read property '" + propertyName + "' from bean", ex);
        }
    }

    /**
     * 设置Bean属性:调用set方法
     *
     * @param bean         bean
     * @param propertyName 属性名
     * @param value        属性值
     */
    public static void setProperty(Object bean, String propertyName, Object value) {
        PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
        if (pd == null) {
            throw new DggUtilException("Could not set property '" + propertyName + "' to bean PropertyDescriptor is null");
        }
        Method writeMethod = pd.getWriteMethod();
        if (writeMethod == null) {
            throw new DggUtilException("Could not set property '" + propertyName + "' to bean writeMethod is null");
        }
        if (!writeMethod.isAccessible()) {
            writeMethod.setAccessible(true);
        }
        try {
            writeMethod.invoke(bean, value);
        } catch (Throwable ex) {
            throw new DggUtilException("Could not set property '" + propertyName + "' to bean", ex);
        }
    }

    /**
     * 给一个Bean添加字段
     *
     * @param superBean 父级Bean
     * @param props     新增属性
     * @return {Object}
     */
    public static Object generator(Object superBean, BeanProperty... props) {
        Class<?> superclass = superBean.getClass();
        Object genBean = generator(superclass, props);
        copy(superBean, genBean);
        return genBean;
    }

    /**
     * 给一个class添加字段
     *
     * @param superclass 父级
     * @param props      新增属性
     * @return {Object}
     */
    public static Object generator(Class<?> superclass, BeanProperty... props) {
        BeanGenerator generator = new BeanGenerator();
        generator.setSuperclass(superclass);
        generator.setUseCache(true);
        for (BeanProperty prop : props) {
            generator.addProperty(prop.getName(), prop.getType());
        }
        return generator.create();
    }

    /**
     * 获取缓存key
     *
     * @param class1
     * @param class2
     * @param useConverter
     * @return
     */
    private static String generateKey(Class<?> class1, Class<?> class2, boolean useConverter) {
        String key = class1.getName() + "@" + class2.getName();
        if (useConverter) {
            key += "@" + useConverter;
        }
        return key;
    }

    /**
     * 获取缓存BeanCopier
     *
     * @param sourceClazz  : 拷贝源类
     * @param targetClazz  : 目标类
     * @param useConverter : 是否使用转换器
     * @return
     */
    private static BeanCopier getBeanCopier(Class sourceClazz, Class targetClazz, boolean useConverter) {
        String cacheKey = generateKey(sourceClazz, targetClazz, useConverter);
        BeanCopier copier = null;
        // 缓存中有BeanCopier 直接拿来用,没有就创建并放入缓存
        if (BEAN_COPIERS.containsKey(cacheKey)) {
            copier = BEAN_COPIERS.get(cacheKey);
        } else {
            copier = BeanCopier.create(sourceClazz, targetClazz, useConverter);
            BEAN_COPIERS.putIfAbsent(cacheKey, copier);
        }
        return copier;
    }

    /**
     * copy 对象属性到另一个对象，默认不使用Convert
     *
     * @param source      : 拷贝源对象
     * @param targetClazz : 目标类名
     * @return T
     */
    public static <T> T copy(Object source, Class<T> targetClazz) {
        BeanCopier copier = getBeanCopier(source.getClass(), targetClazz, false);
        T to = newInstance(targetClazz);
        copier.copy(source, to, null);
        return to;
    }

    /**
     * 使用默认转换器拷贝对象
     *
     * @param source       : 拷贝源对象
     * @param targetClazz  : 目标类名
     * @param useConverter : 是否使用转换器
     * @return T
     */
    public static <T> T copy(Object source, Class<T> targetClazz, boolean useConverter) {
        if (useConverter) {
            return copy(source, targetClazz, DeepCopyConverter.build(targetClazz));
        } else {
            return copy(source, targetClazz);
        }
    }

    /**
     * copy 对象属性到另一个对象，使用Convert
     *
     * @param source      : 拷贝源对象
     * @param targetClazz : 目标类名
     * @param converter   : 转换器
     * @return T
     */
    public static <T> T copy(Object source, Class<T> targetClazz, Converter converter) {
        T to = newInstance(targetClazz);
        BeanCopier copier = getBeanCopier(source.getClass(), targetClazz, true);
        copier.copy(source, to, converter);
        return to;
    }

    /**
     * 拷贝对象
     *
     * @param source : 拷贝源对象
     * @param target : 目标对象
     */
    public static void copy(Object source, Object target) {
        BeanCopier copier = getBeanCopier(source.getClass(), target.getClass(), false);
        copier.copy(source, target, null);
    }

    /**
     * 使用默认转换器拷贝对象
     *
     * @param source       : 拷贝源对象
     * @param target       : 目标对象
     * @param useConverter : 是否使用转换器
     */
    public static void copy(Object source, Object target, boolean useConverter) {
        if (useConverter) {
            copy(source, target, DeepCopyConverter.build(target.getClass()));
        } else {
            copy(source, target);
        }
    }

    /**
     * 拷贝对象
     *
     * @param source    : 拷贝源对象
     * @param target    : 目标对象
     * @param converter : 转换器
     */
    public static void copy(Object source, Object target, Converter converter) {
        BeanCopier copier = getBeanCopier(source.getClass(), target.getClass(), true);
        copier.copy(source, target, converter);
    }

    /**
     * 获取bean 属性
     *
     * @param clazz : 对象
     * @return
     */
    public static List getPropertyName(Class<?> clazz) {
        List<String> list = new ArrayList<>();
        PropertyDescriptor[] beanPds = getPropertyDescriptors(clazz);
        for (PropertyDescriptor beanPd : beanPds) {
            String name = beanPd.getName();
            if (name.equalsIgnoreCase("serialVersionUID") || name.equalsIgnoreCase("Class")) {
                continue;
            }
            list.add(name);
        }
        return list;
    }

    /**
     * 获取bean 属性和属性类型
     *
     * @param clazz : 对象
     * @return
     */
    public static Map getNameAndType(Class<?> clazz) {
        Map<String, String> map = new HashMap<>();
        PropertyDescriptor[] beanPds = getPropertyDescriptors(clazz);
        for (PropertyDescriptor beanPd : beanPds) {
            String typeName = beanPd.getPropertyType().getTypeName();
            if (typeName.equalsIgnoreCase("java.lang.Class")) {
                continue;
            }
            String name = beanPd.getName();
            if (name.equalsIgnoreCase("serialVersionUID")) {
                continue;
            }
            map.put(name, typeName);
        }
        return map;
    }

    /**
     * 获取类属性和属性类型（类树形结构）
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
                allFields = DggArrayUtil.append(allFields, declaredFields);
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
     * 将对象装成map形式
     *
     * @param src
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static Map toMap(Object src) {
        return BeanMap.create(src);
    }

    /**
     * 将map 转为 bean
     */
    public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
        T bean = newInstance(valueType);
        PropertyDescriptor[] beanPds = getPropertyDescriptors(valueType);
        for (PropertyDescriptor propDescriptor : beanPds) {
            String propName = propDescriptor.getName();
            // 过滤class属性
            if (propName.equals("class")) {
                continue;
            }
            if (beanMap.containsKey(propName)) {
                Method writeMethod = propDescriptor.getWriteMethod();
                if (null == writeMethod) {
                    continue;
                }
                Object value = beanMap.get(propName);
                if (!writeMethod.isAccessible()) {
                    writeMethod.setAccessible(true);
                }
                try {
                    writeMethod.invoke(bean, value);
                } catch (Throwable e) {
                    throw new DggUtilException("Could not set property '" + propName + "' to bean", e);
                }
            }
        }
        return bean;
    }

    /**
     * 将map 转为 bean,以驼峰命名返回
     *
     * @param beanMap
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> T toBeanHaveHump(Map<String, Object> beanMap, Class<T> valueType) {
        Set<String> keySet = beanMap.keySet();
        T t = null;
        try {
            t = valueType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Field[] declaredFields = valueType.getDeclaredFields();
        for (String key : keySet) {
            String s = DggStringUtil.underlineToCamel(key);
            for (Field field : declaredFields) {
                String fieldName = DggStringUtil.underlineToCamel(field.getName());
                if (s.equals(fieldName)) {
                    field.setAccessible(true);
                    try {
                        field.set(t, beanMap.get(key));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return t;
    }

    /**
     * 将map key转为驼峰命名返回
     *
     * @param beanMap
     * @return
     */
    public static Map<String, Object> toHaveHumpKey(Map<String, Object> beanMap) {
        Set<String> keySet = beanMap.keySet();
        Map<String, Object> map = new HashMap<>();
        keySet.forEach(key -> {
            String fieldName = DggStringUtil.underlineToCamel(key, false);
            try {
                map.put(fieldName, JSONArray.parseArray((String) beanMap.get(key)));
            } catch (Exception e) {
                try {
                    map.put(fieldName, JSONObject.parseObject((String) beanMap.get(key)));
                } catch (Exception e1) {
                    map.put(fieldName, beanMap.get(key));
                }
            }
        });
        return map;
    }


    /**
     * 初始化类的相关信息
     *
     * @param clazz : java类型
     * @param info  :
     */
    public static void setClassInfo(Class<?> clazz, String info) throws IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        //1.实例化对象
        Object obj = clazz.newInstance();
        String[] name = info.split(",");//获取所有的属性-值的键值对：记住所有的名字与属性值一样
        for (int i = 0; i < name.length; i++) {
            String[] value = name[i].split(":");//获取具体的属性：值

            //2，根据属性名获取相应属性
            Field field = clazz.getDeclaredField(value[0]);
            //3.根据属性名，和属性的类型获取相应的setter方法
            Method method = clazz.getMethod("set" + initcap(value[0]), field.getType());
            //4.根据属性的类型进行赋值
            String simplename = field.getType().getSimpleName();
            method.invoke(obj, DggStringUtil.getValues(simplename, value[1]));
        }
        //打印设置的数据
        System.out.println(obj.toString());

        //重新通过属性直接设置值
        System.out.println("类中的值赋值后为:");
        Field[] fields = clazz.getDeclaredFields();
        for (int m = 0; m < fields.length; m++) {
            //确保私有的属性也可以赋值
            fields[m].setAccessible(true);
            fields[m].set(obj, DggStringUtil.getValues(fields[m].getType().getSimpleName()));
            System.out.print(fields[m].getName() + "=" + fields[m].get(obj) + ";");
        }
    }

    public static void setInterfaceInfo(Class<?> clazz) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        //1.获取所继承的所有接口
        Class<?>[] interfaces = clazz.getInterfaces();
        //2.接口中的方法是在子类中进行调用的，所以需要获取子类的实例化对象
        Object obj = clazz.newInstance();
        for (int i = 0; i < interfaces.length; i++) {
            //3.获取接口中所有的方法
            Method[] methods = interfaces[i].getMethods();
            for (int j = 0; j < methods.length; j++) {
                //4.获取方法中所有的参数
                Class<?>[] parameterTypes = methods[j].getParameterTypes();
                //用于保存要传递的数值
                Object[] values = new Object[parameterTypes.length];
                for (int k = 0; k < parameterTypes.length; k++) {
                    //5.根据types进行传值
                    values[k] = DggStringUtil.getValues(parameterTypes[k].getSimpleName());
                }

                //6.最重要的一步：是子类进行调用方法，所以需要通过子类的class获取方法
                Method method = clazz.getMethod(methods[j].getName(), parameterTypes);
                method.invoke(obj, values);
            }
            //7获取接口中的相关属性
            Field[] fields = interfaces[i].getFields();
            for (int n = 0; n < fields.length; n++) {
                fields[i].setAccessible(true);
                System.out.println(fields[n].getName() + "=" + fields[n].get(obj));
            }
        }
    }


    public static String initcap(String name) {
        if (name == null || name.equals("")) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * describe: Bean属性
     * version 1.0
     * Created by hot.sun on 2018/7/4 .
     **/
    public static class BeanProperty {
        private final String name;
        private final Class<?> type;

        public BeanProperty(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }
    }

    /**
     * 默认复制转换器
     */
    public static class DeepCopyConverter implements Converter {

        public static DeepCopyConverter build(Class<?> target) {
            return new DeepCopyConverter(target);
        }

        /**
         * The Target.
         */
        private Class<?> target;

        /**
         * Instantiates a new Deep copy converter.
         *
         * @param target the target
         */
        public DeepCopyConverter(Class<?> target) {
            this.target = target;
        }

        @Override
        public Object convert(Object bean, Class targetClazz, Object methodName) {
            // bean属性类型为List
            if (bean instanceof List) {
                List values = (List) bean;
                List retList = new ArrayList<>(values.size());
                for (final Object source : values) {
                    String tempFieldName = methodName.toString().replace("set",
                            "");
                    String fieldName = tempFieldName.substring(0, 1)
                            .toLowerCase() + tempFieldName.substring(1);
                    Class clazz = DggClassUtil.getElementType(target, fieldName);
                    retList.add(copy(source, clazz, true));
                }
                return retList;
            }
            // bean属性类型为Map
            if (bean instanceof Map) {
                // TODO 暂时用不到，后续有需要再补充
                return bean;
            }
            // bean属性类型不为基础类型和包装类型
            if (!DggClassUtil.isPrimitive(targetClazz)) {
                return copy(bean, targetClazz, true);
            }
            return bean;
        }
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
