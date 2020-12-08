package net.dgg.utils.context;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import java.util.UUID;

/**
 * @Description: 上下文环境
 * @Author Created by yan.x on 2020-05-11 .
 **/
public class DggSpringContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.environment = applicationContext.getEnvironment();
    }

    public Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(name, requiredType);
    }

    public Object getBean(String name, Object... args) throws BeansException {
        return applicationContext.getBean(name, args);
    }

    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }

    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return applicationContext.getBean(requiredType, args);
    }

    public boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }

    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isPrototype(name);
    }

    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return applicationContext.isTypeMatch(name, typeToMatch);
    }

    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return applicationContext.isTypeMatch(name, typeToMatch);
    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }

    public String[] getAliases(String name) {
        return applicationContext.getAliases(name);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Environment getEnvironment() {
        return environment;
    }

    /**
     * <p>是否存在key对应的属性值</p>
     *
     * @param key : 属性key
     * @return {@code true} 如果存在则返回.
     */
    public boolean containsProperty(String key) {
        return environment.containsProperty(key);
    }

    /**
     * <p>获取属性对应的属性值</p>
     *
     * @param key : 属性key
     * @return
     */
    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    /**
     * <p>获取属性对应的属性值</p>
     *
     * @param key          : 属性key
     * @param defaultValue : 如果没有找到任何值，则返回缺省值
     * @return
     */
    public String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    /**
     * <p>获取属性对应的属性值</p>
     *
     * @param key        : 属性key
     * @param targetType : 返回的指定对象封装
     * @param <T>
     * @return
     */
    public <T> T getProperty(String key, Class<T> targetType) {
        return environment.getProperty(key, targetType);
    }

    /**
     * <p>获取属性对应的属性值</p>
     *
     * @param key          : 属性key
     * @param targetType   : 返回的指定对象封装
     * @param defaultValue : 如果没有找到任何值，则返回缺省值
     * @param <T>
     * @return
     */
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return environment.getProperty(key, targetType, defaultValue);
    }

    /**
     * 根据bean的class获取实例对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getInstanceBean(Class<T> clazz) {
        if (null == applicationContext) {
            return null;
        }
        T _t = null;
        try {
            String beanName = clazz.getSimpleName() + UUID.randomUUID();
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
            _t = applicationContext.getBean(beanName, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return _t;
    }

    public static synchronized <T> T getDynamicBean(Class<T> clazz) {
        if (null == applicationContext) {
            return null;
        }
        T _t = null;
        boolean hasNoInjectionBean = false;
        try {
            _t = applicationContext.getBean(clazz);
            if (_t == null) {
                hasNoInjectionBean = true;
            }
        } catch (Exception e) {
            hasNoInjectionBean = true;
        }
        if (hasNoInjectionBean) {
            try {
                String beanName = clazz.getSimpleName();
                DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClass(clazz);
                beanFactory.registerBeanDefinition(beanName, beanDefinition);
                beanFactory.registerSingleton(beanName, clazz.newInstance());
                _t = applicationContext.getBean(clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return _t;
    }
}