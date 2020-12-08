package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

/**
 * @Author: linhongda
 * @Date: 2019/5/20 0020
 * @Description: 交换器类型枚举类
 */
public enum RabbitType {

    // 点对点交换器类型
    DIRECT("direct"),
    // 全局广播交换器类型
    FANOUT("fanout");

    private final String type;

    RabbitType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
