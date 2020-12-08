package net.dasnotsad.framework.data;

import java.lang.annotation.*;

/**
 * 定义ES的实体
 *
 * @author liuliwei
 * @create 2020-08-05
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EsEntity {

    /**
     * 索引名称
     */
    String indexName();

    /**
     * type名称
     */
    String type() default "defaultType";

    /**
     * 默认分片数
     */
    short shards() default 3;

    /**
     * 默认副本数
     */
    short replicas() default 2;

    /**
     * 检测索引是否存在，不存在则按照本注解参数创建
     */
    boolean createIndex() default false;

    /**
     * 数据源标识，默认0
     */
    int whichDataSource() default 0;
}
