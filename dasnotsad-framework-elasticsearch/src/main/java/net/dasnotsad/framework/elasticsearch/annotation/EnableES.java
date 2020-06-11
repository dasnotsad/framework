package net.dasnotsad.framework.elasticsearch.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ElasticsearchProperties.class, ESRestBuilderConfiguration.class})
@Documented
@Inherited
public @interface EnableES {
}
