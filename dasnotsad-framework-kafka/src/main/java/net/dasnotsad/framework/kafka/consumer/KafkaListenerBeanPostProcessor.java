package net.dasnotsad.framework.kafka.consumer;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import net.dasnotsad.framework.kafka.annotation.KafkaListener;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * kafka消费者注解实现
 *
 * @author liuliwei
 * @create 2019-05-31
 */
public class KafkaListenerBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private DasnotsadKafkaConsumer dasnotsadKafkaConsumer;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> clazz = AopUtils.getTargetClass(bean);
        Map<Method, KafkaListener> annotatedMethods = MethodIntrospector.selectMethods(clazz,
                (MetadataLookup<KafkaListener>) method -> AnnotatedElementUtils.findMergedAnnotation(method, KafkaListener.class));
        if (!annotatedMethods.isEmpty()) {
            for (Entry<Method, KafkaListener> entry : annotatedMethods.entrySet()) {
                dasnotsadKafkaConsumer.consumeAsync(bean, entry.getKey(), entry.getValue());
            }
        }
        return bean;
    }

}