package net.dasnotsad.framework.kafka.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import net.dasnotsad.framework.kafka.producer.KafkaProducerFactory;

/**
 * kafka多数据源容器
 *
 * @author liuliwei
 * @create 2019-05-31
 */
public class KafkaContainer {

	public static final int DEFAULT_DATASOURCE = 0;
	private Map<Integer, KafkaProducerFactory> containers = new LinkedHashMap<>();
	private Properties producerProperties;

	public KafkaContainer(Properties producerProperties){
		this.producerProperties = producerProperties;
	}

	public KafkaProducerFactory get(int whichDataSource) {
		KafkaProducerFactory rc = containers.get(whichDataSource);
		Objects.requireNonNull(rc, "whichDataSource=" + whichDataSource + " not found");
		return rc;
	}

	public int getWhichDataSource(String nodes) {
		for (Entry<Integer, KafkaProducerFactory> entry : containers.entrySet()) {
			if (entry.getValue().getNodes().equals(nodes)) {
				return entry.getKey();
			}
		}
		int whichDataSource = dscode(nodes);
		if (containers.containsKey(whichDataSource)) {
			return whichDataSource;
		}
		put(whichDataSource, nodes);
		return whichDataSource;
	}

	public void put(int whichDataSource, KafkaProducerFactory kafkaProducerFactory) {
		containers.put(whichDataSource, kafkaProducerFactory);
	}

	public void put(int whichDataSource, String nodes) {
		containers.put(whichDataSource, new KafkaProducerFactory(nodes, producerProperties));
	}

	private int dscode(String nodes) {
		Objects.requireNonNull(nodes, "dscode['nodes'] must not be null");
		return Math.abs(nodes.hashCode());
	}

}
