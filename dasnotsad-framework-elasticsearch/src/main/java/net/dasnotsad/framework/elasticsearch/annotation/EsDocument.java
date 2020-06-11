package net.dasnotsad.framework.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.dasnotsad.framework.elasticsearch.ESTemplate;

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
	 * 索引名称
	 */
	String indexName();

	/**
	 * type名称
	 */
	String type() default "";

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