package net.dasnotsad.framework.tac.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 索引es对象的id标识
 *
 * @author liuliwei
 * @create 2018-11-21
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EsIdentify {
}