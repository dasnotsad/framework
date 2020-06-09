package net.dasnotsad.framework.kafka.producer;

import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * kafka生产者异步发送callback，需继承使用
 *
 * @author liuliwei
 * @create 2018-12-18
 */

@FunctionalInterface
public interface ProducerAsyncHandler {

    void onCompletion(RecordMetadata metadata, Exception exception);
}