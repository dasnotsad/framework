package net.dasnotsad.framework.kafka.annotation;

import net.dasnotsad.framework.kafka.config.KafkaContainer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KafkaListener {

    /**
     * Kafka topic.
     *
     * @return
     */
    String topicName() default "";

    /**
     * Kafka topic arrays.
     *
     * @return
     */
    String[] topicNames() default {};

    /**
     * Kafka group.id.
     *
     * @return
     */
    String groupId() default "defaultGroupId";

    /**
     * Number of consumer.
     *
     * @return
     */
    int consumerNum() default 1;

    /**
     * Number of consumer threads.
     *
     * @return
     */
    int threadNum() default 1;

    /**
     * 数据源，默认值0，即第1个
     *
     * @return
     */
    int whichDataSource() default KafkaContainer.DEFAULT_DATASOURCE;
    
    /**
     * 是否广播
     * @return
     */
    boolean broadcast() default false;
}
