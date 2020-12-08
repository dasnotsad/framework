package net.dasnotsad.framework.tac.data.enums;

/**
 * 排序方式枚举
 *
 * @author liuliwei
 * @create 2020-08-07
 */
public enum Sort {

    /**
     * 正序排列
     */
    ASC("asc"),

    /**
     * 倒序排列
     */
    DESC("desc");

    private String value;

    Sort(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}