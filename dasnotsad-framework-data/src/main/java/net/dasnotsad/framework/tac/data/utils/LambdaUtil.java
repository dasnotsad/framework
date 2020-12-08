package net.dasnotsad.framework.tac.data.utils;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import net.dasnotsad.framework.tac.data.lambda.SFunction;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lambda解析工具类
 *
 * @author liuliwei
 * @create 2020-08-07
 */
@Slf4j
public class LambdaUtil {

    private static Map<Class, SerializedLambda> CLASS_LAMDBA_CACHE = new WeakHashMap<>(10);

    private static Map<Integer, Map<String, String>> COLUMN_CACHE = new ConcurrentHashMap<>(10);

    /**
     * 将传入的lambda解析为属性名
     *
     * @param func 需要解析的 lambda 对象
     * @return 返回解析后的结果
     */
    public static <T> String convertToFieldName(SFunction<T, ?> func){
        var lambda = resolve(func);
        return convertToFieldName(lambda);
    }

    /**
     * 解析 lambda 表达式, 在此基础上加了缓存。
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    private static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        var clazz = func.getClass();
        return Optional.ofNullable(CLASS_LAMDBA_CACHE.get(clazz))
                .orElseGet(() -> {
                    SerializedLambda lambda = getSerializedLambda(func);
                    CLASS_LAMDBA_CACHE.put(clazz, lambda);
                    return lambda;
                });
    }

    /***
     * 转换方法引用为属性名
     * @param lambda lambda表达式
     * @return
     */
    private static String convertToFieldName(SerializedLambda lambda){
        var hs = lambda.hashCode();
        var columnMap = COLUMN_CACHE.get(hs);
        if(columnMap != null){
            var columnName = columnMap.get(lambda.getImplMethodName());
            if(columnName != null)
                return columnName;
            columnName = convertColumnName(lambda);
            columnMap.put(lambda.getImplMethodName(), columnName);
            return columnName;
        }
        columnMap = new HashMap<>();
        var columnName = convertColumnName(lambda);
        columnMap.put(lambda.getImplMethodName(), columnName);
        COLUMN_CACHE.put(hs, columnMap);
        return columnName;
    }

    //根据lambda表达式获取字段名
    private static String convertColumnName(SerializedLambda lambda){
        var methodName = lambda.getImplMethodName();
        String prefix = null;
        if(methodName.startsWith("get")){
            prefix = "get";
        } else if(methodName.startsWith("is")){
            prefix = "is";
        } else if(methodName.startsWith("set")){
            prefix = "set";
        }
        if(prefix == null){
            log.warn("无效的getter方法: " + methodName);
        }
        return toLowerCaseFirstOne(prefix != null ? methodName.replace(prefix, "") : methodName);
    }

    /**
     * 序列化为Lambda对象
     * @param func getter
     */
    private static SerializedLambda getSerializedLambda(Serializable func) {
        var lambda = CLASS_LAMDBA_CACHE.get(func.getClass());
        if(lambda == null) {
            try {
                var method = func.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                lambda = (SerializedLambda) method.invoke(func);
                CLASS_LAMDBA_CACHE.put(func.getClass(), lambda);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error("cause exception:", e.getMessage(), e);
            }
        }
        return lambda;
    }

    private static String toLowerCaseFirstOne(String str){
        if(StringUtils.isEmpty(str))
            return str;
        var firstChar = String.valueOf(str.charAt(0));
        return str.replaceFirst(firstChar, firstChar.toLowerCase());
    }
}
