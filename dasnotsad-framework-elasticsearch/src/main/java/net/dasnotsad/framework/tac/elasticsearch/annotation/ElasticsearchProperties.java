package net.dasnotsad.framework.tac.elasticsearch.annotation;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dasnotsad.paas")
public class ElasticsearchProperties implements InitializingBean{

	private Map<Integer, String> elasticsearch = new LinkedHashMap<>();

	public Map<Integer, String> getElasticsearch() {
		return elasticsearch;
	}

	public void setElasticsearch(Map<Integer, String> elasticsearch) {
		this.elasticsearch = elasticsearch;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

}
