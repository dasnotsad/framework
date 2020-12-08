package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

/**
 * @Author: linhongda
 * @Date: 2019/11/6 0006
 * @Description: 数据源类型
 */
public enum DatasourceType {
    /**
     * ES数据源
     */
    ELASTICSEARCH("elasticsearch"),

    /**
     * mongo数据源
     */
    MONGODB("mongodb");

    DatasourceType(String name){
            this.name = name;
    }
    private final String name;

    public String getName() {
        return name;
    }
}
