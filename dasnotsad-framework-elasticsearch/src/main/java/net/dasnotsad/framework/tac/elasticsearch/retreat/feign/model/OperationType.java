package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

/**
 * @Author: linhongda
 * @Date: 2019/11/6 0006
 * @Description: 操作类型
 */
public enum OperationType {
    /**
     * 写入
     */
    INSERT("insert"),

    /**
     * 异步写入
     */
    ASYNCINSERT("asyncinsert"),

    /**
     * 修改
     */
    UPDATE("update"),

    /**
     * 删除
     */
    DELETE("delete"),

    /**
     * 批处理
     */
    BULK("bulk");

    OperationType(String optType){
        this.optType = optType;
    }
    private final String optType;

    public String getOptType() {
        return optType;
    }
}
