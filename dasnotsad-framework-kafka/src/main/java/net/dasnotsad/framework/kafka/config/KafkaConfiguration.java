package net.dasnotsad.framework.kafka.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.dasnotsad.framework.kafka.consumer.KafkaListenerBeanPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import net.dasnotsad.framework.kafka.producer.KafkaProducerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * kafka注解启动类，装载所需配置
 *
 * @author liuliwei
 * @create 2018-12-21
 * @modify 2019-05-31
 */
@ComponentScan("net.dasnotsad.framework.kafka")
@Configuration
public class KafkaConfiguration {

	@Autowired
	private KafkaProperties kafkaProperties;

	@Bean
	public KafkaContainer kafkaContainer() {
		Properties producerProperties = getProducerProperties();
		KafkaContainer containers = new KafkaContainer(producerProperties);
		for (Entry<Integer, String> entry : kafkaProperties.getKafka().entrySet()) {
			containers.put(entry.getKey(), new KafkaProducerFactory(entry.getValue(), producerProperties));
		}
		return containers;
	}

    private Properties getProducerProperties() {
        Map<String, Object> producerMap = (Map<String, Object>) loadKafka().get("producer");
        Properties producer = new Properties();// 生产者配置项
        producerMap.forEach((key, value) -> producer.put(key, value.toString()));
        return producer;
    }

    @Bean("consumerProperties")
    public Properties getConsumerProperties() {
        Map<String, Object> consumerMap = (Map<String, Object>) loadKafka().get("consumer");
        Properties consumer = new Properties();// 消费者配置项
        consumerMap.forEach((key, value) -> consumer.put(key, value.toString()));
        return consumer;
	}

	private Map<String, Object> loadKafka() {
	    Map<String, Object> app_config = new Yaml()
                  .load(this.getClass().getClassLoader().getResourceAsStream("config/app_config.yml"));
	    return (Map<String, Object>) app_config.get("kafka");
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public KafkaListenerBeanPostProcessor kafkaListenerBeanPostProcessor() {
		return new KafkaListenerBeanPostProcessor();
	}
}