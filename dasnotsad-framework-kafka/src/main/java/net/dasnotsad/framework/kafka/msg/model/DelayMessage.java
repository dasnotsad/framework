package net.dasnotsad.framework.kafka.msg.model;

import lombok.Data;

/**
 * @Author: liuliwei
 * @Date: 2019-11-5
 * @Description: Kafka延迟生产参数
 */
@Data
public class DelayMessage {
    /**
     * kafka topic name
     */
    private String topic;
    /**
     * 系统码
     */
    private String sys_code;
    /**
     * 消息体
     */
    private Object data;
    /**
     * 将要发送的时间戳
     */
    private Long delay_time;
    /**
     * 数据源信息
     */
    private String nodes;
}
