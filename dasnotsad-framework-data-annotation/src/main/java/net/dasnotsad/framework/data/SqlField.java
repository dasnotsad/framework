package net.dasnotsad.framework.data;

import java.lang.annotation.*;

/**
 * 定义MySql、TiDB等关系型数据库的字段
 *
 * @author liuliwei
 * @create 2020-08-05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SqlField {

    /**
     * 该对象属性对应的数据库字段名
     */
    String value() default "";
}
