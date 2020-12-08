package net.dasnotsad.framework.tac.elasticsearch.annotation;

import net.dasnotsad.framework.tac.elasticsearch.ESTemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * es bean操作注解
 *
 * @author liuliwei
 * @create 2019-8-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EsDocument {
    /**
     * 检索时的索引名称，如果不配置则默认为和indexName一致，该注解项仅支持搜索
     * 并不建议这么做，建议通过特定方法来做跨索引查询
     */
    String[] searchIndexNames() default {};

    /**
     * 索引名称
     */
    String indexName() default "default_index";

    /**
     * 默认分片数
     */
    short shards() default 5;

    /**
     * 默认副本数
     */
    short replicas() default 1;

    /**
     * 检测索引是否存在，不存在则按照本注解参数创建
     */
    boolean createIndex() default false;

    int whichDataSource() default ESTemplate.DEFAULT_DATASOURCE;
}