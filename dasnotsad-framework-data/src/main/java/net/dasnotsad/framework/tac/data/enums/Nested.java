package net.dasnotsad.framework.tac.data.enums;

/**
 * 逻辑连接枚举
 *
 * @author liuliwei
 * @create 2020-08-31
 */
public enum Nested {

    /**
     * 逻辑连接：且
     */
    AND("and"),

    /**
     * 逻辑连接：或
     */
    OR("or");

    private String value;

    Nested(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
