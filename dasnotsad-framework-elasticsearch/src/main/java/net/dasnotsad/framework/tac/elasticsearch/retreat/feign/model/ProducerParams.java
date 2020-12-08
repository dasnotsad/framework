package net.dasnotsad.framework.tac.elasticsearch.retreat.feign.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: linhongda
 * @Date: 2019/8/16 0016
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProducerParams<T> {
    /**
     * 交换器名称
     */
    private String exchangeName;
    /**
     * 队列名称
     */
    private String queueName;
    /**
     * 传输数据
     */
    private T data;
}
