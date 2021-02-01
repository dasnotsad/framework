package net.dasnotsad.framework.tac.elasticsearch.core.enums;

import org.springframework.util.StringUtils;

/**
 * @Description: es字段数据结构
 * @Author Created by yan.x on 2020-07-22 .
 **/
public enum FieldType {
    keyword_type("keyword"),
    text_type("text"),
    text_nokeyword_type("text_nokeyword"),
    byte_type("byte"),
    short_type("short"),
    integer_type("integer"),
    long_type("long"),
    float_type("float"),
    double_type("double"),
    boolean_type("boolean"),
    nested_type("nested"),
    date_type("date");

    private final String code;

    FieldType(String code) {
        this.code = code;
    }

    public static FieldType get(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (FieldType value : values()) {
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
