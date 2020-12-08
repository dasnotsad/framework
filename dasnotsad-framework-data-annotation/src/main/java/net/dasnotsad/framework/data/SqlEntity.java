package net.dasnotsad.framework.data;

import java.lang.annotation.*;

/**
 * 定义MySql、TiDB等关系型数据库的实体
 *
 * @author liuliwei
 * @create 2020-08-05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SqlEntity {

    /**
     * 实体对应的表名
     */
    String value() default "";

    /**
     * 数据源标识，默认0
     */
    int whichDataSource() default 0;
}
