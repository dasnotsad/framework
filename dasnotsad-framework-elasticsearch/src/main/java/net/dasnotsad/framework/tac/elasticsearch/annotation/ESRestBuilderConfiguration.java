package net.dasnotsad.framework.tac.elasticsearch.annotation;

import net.dasnotsad.framework.tac.elasticsearch.repository.SimpleElasticSearchRepository;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.Map;

@ComponentScan("net.dasnotsad.framework.tac.elasticsearch")
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

    @Lazy
    @Bean
    @ConditionalOnMissingBean
    public SimpleElasticSearchRepository simpleElasticSearchRepository() {
        return new SimpleElasticSearchRepository();
    }

    /**
     * @Description: 通过反射强制更改rest请求的超时时间
     */
    @Bean
    public RequestOptions defaultRequestOptions() throws NoSuchFieldException, IllegalAccessException {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        RequestConfig rc = RequestConfig.DEFAULT;

        Field field0 = rc.getClass().getDeclaredField("connectionRequestTimeout");
        field0.setAccessible(true);
        field0.set(rc, 3000);

        Field field1 = rc.getClass().getDeclaredField("connectTimeout");
        field1.setAccessible(true);
        field1.set(rc, 3000);

        Field field2 = rc.getClass().getDeclaredField("socketTimeout");
        field2.setAccessible(true);
        field2.set(rc, 3000);

        builder.setRequestConfig(rc);
        //针对gzip支持
        return builder.addHeader("Accept-Encoding", "gzip, deflate").build();
    }

    @Bean("ribbonRestTemplate")
    @ConditionalOnClass(name = {"org.springframework.cloud.client.loadbalancer", "org.springframework.web.client.RestTemplate"})
    @ConditionalOnMissingBean(name = "ribbonRestTemplate")
    @LoadBalanced
    public RestTemplate ribbonRestTemplate() {
        return new RestTemplate();
    }
}
