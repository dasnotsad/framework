package net.dasnotsad.framework.tac.data.enums;

/**
 * 查询语句条件定义枚举
 *
 * @author liuliwei
 * @create 2020-08-13
 */
public enum Query {

    eq("eq"),

    ne("ne"),

    gt("gt"),

    gte("gte"),

    lt("lt"),

    lte("lte"),

    rightLike("rightLike"),

    leftLike("leftLike"),

    allLike("allLike");

    private String value;

    Query(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
