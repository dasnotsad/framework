package net.dasnotsad.framework.kafka.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.dasnotsad.framework.kafka.config.KafkaConfiguration;
import net.dasnotsad.framework.kafka.config.KafkaProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * kafka启动用注解
 *
 * @author liuliwei
 * @create 2018-12-21
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ KafkaProperties.class, KafkaConfiguration.class })
@Documented
@Inherited
@EnableAsync
public @interface EnableKafka {
}