package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

/**
 * @Author: linhongda
 * @Date: 2019/11/6 0006
 * @Description: 记录状态
 */
public enum RecordStatus {

    WAIT("wait"),//等待中
    SUCCESS("success"),//执行成功
    ERROR("error"),//发生错误（放弃）
    RETRYING("retrying");//尝试重试中

    RecordStatus(String name){this.name = name;}

    private final String name;

    public String getName() {
        return name;
    }
}
