package net.dasnotsad.framework.tac.elasticsearch.metadata;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: mapping注解对应的数据载体类
 * @Author Created by yan.x on 2020-07-22 .
 **/
@Getter
@Setter
public class MappingData {

    private String fieldName;

    /**
     * 数据类型（包含 关键字类型）
     *
     * @return
     */
    private String dataType;
    /**
     * 索引分词器设置
     *
     * @return
     */
    private String analyzer;
    /**
     * 间接关键字
     *
     * @return
     */
    //private boolean keyWord;

    /**
     * 关键字忽略字数
     *
     * @return
     */
    //private int ignoreAbove;
    /**
     * 是否支持autocomplete，高效全文搜索提示
     *
     * @return
     */
    //private boolean autocomplete;
    /**
     * 是否支持suggest，高效前缀搜索提示
     *
     * @return
     */
    //private boolean suggest;
    /**
     * 搜索内容分词器设置
     *
     * @return
     */
    //private String searchAnalyzer;

    //private boolean allowSearch;

    //private String copyTo;

}
