package net.dasnotsad.framework.tac.elasticsearch.annotation;

import net.dasnotsad.framework.tac.elasticsearch.core.enums.Analyzer;
import net.dasnotsad.framework.tac.elasticsearch.core.enums.FieldType;

import java.lang.annotation.*;

/**
 * @Description: 对应索引结构mapping的注解，在es entity field上添加
 * @Author Created by yan.x on 2020-07-22 .
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface EsField {
    /**
     * 数据类型（包含 关键字类型）
     */
    FieldType fieldType() default FieldType.text_type;

    /**
     * 索引分词器设置
     */
    Analyzer analyzer() default Analyzer.no_analyzer;

    /**
     * 间接关键字
     */
    //boolean keyword() default true;

    /**
     * 关键字忽略字数
     */
    //int ignore_above() default 256;

    /**
     * 是否支持autocomplete，高效全文搜索提示
     */
    //boolean autocomplete() default false;

    /**
     * 是否支持suggest，高效前缀搜索提示
     */
    //boolean suggest() default false;

    /**
     * 搜索内容分词器设置
     */
    //Analyzer search_analyzer() default Analyzer.standard_analyzer;

    /**
     * 是否允许被搜索
     */
    //boolean allow_search() default true;

    /**
     * 拷贝到哪个字段，代替_all
     */
    //String copy_to() default "";
}
