package net.dasnotsad.framework.elasticsearch.annotation;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("net.dasnotsad.framework.elasticsearch")
@Configuration
public class ESRestBuilderConfiguration {

	@Autowired
	private ElasticsearchProperties elasticsearchProperties;

	@Bean
	public RestClientContainer restClientContainer() {
		RestClientContainer containers = new RestClientContainer();
		for (Map.Entry<Integer, String> entry : elasticsearchProperties.getElasticsearch().entrySet()) {
			containers.put(entry.getKey(), entry.getValue());
		}
		return containers;
	}

}
