package net.dasnotsad.framework.kafka.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;;

@ConfigurationProperties(prefix = "dasnotsad.paas")
public class KafkaProperties implements InitializingBean{

	private Map<Integer, String> kafka = new LinkedHashMap<Integer, String>();

	public Map<Integer, String> getKafka() {
		return kafka;
	}

	public void setKafka(Map<Integer, String> kafka) {
		this.kafka = kafka;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}
}
