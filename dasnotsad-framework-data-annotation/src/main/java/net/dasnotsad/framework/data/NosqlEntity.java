package net.dasnotsad.framework.data;

import java.lang.annotation.*;

/**
 * 定义mongoDB等非关系型数据库的实体
 *
 * @author liuliwei
 * @create 2020-08-05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NosqlEntity {

    /**
     * 实体对应的集合名
     */
    String value() default "";

    /**
     * 数据源标识，默认0
     */
    int whichDataSource() default 0;
}
