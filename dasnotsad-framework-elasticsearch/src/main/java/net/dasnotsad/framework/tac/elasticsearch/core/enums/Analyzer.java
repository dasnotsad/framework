package net.dasnotsad.framework.tac.elasticsearch.core.enums;

import org.springframework.util.StringUtils;

/**
 * @Description: 分词器类型
 * @Author Created by yan.x on 2020-07-22 .
 **/
public enum Analyzer {
    //支持中文采用的方法为单字切分。他会将词汇单元转换成小写形式，并去除停用词和标点符号
    standard_analyzer("standard"),
    //首先会通过非字母字符来分割文本信息，然后将词汇单元统一为小写形式。该分析器会去掉数字类型的字符
    simple_analyzer("simple"),
    //仅仅是去除空格，对字符没有lowcase化,不支持中文
    whitespace_analyzer("whitespace"),
    stop_analyzer("stop"),
    keyword_analyzer("keyword"),
    pattern_analyzer("pattern"),
    fingerprint_analyzer("fingerprint"),
    //ik中文分词
    ik_max_word_analyzer("ik_max_word"),

    no_analyzer("");


    private final String code;

    Analyzer(String code) {
        this.code = code;
    }

    public static Analyzer get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (Analyzer value : values()) {
            if (value.getCode().equalsIgnoreCase(code)) {
                return value;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

}
