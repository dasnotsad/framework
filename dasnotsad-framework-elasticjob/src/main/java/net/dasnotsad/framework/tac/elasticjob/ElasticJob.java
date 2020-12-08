package net.dasnotsad.framework.tac.elasticjob;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * 定时任务类配置使用的注解
 * 
 * @author liuliwei
 * @create 2020-09-15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ ElasticJobProperties.class, ElasticJobConfiguration.class })
public @interface ElasticJob {

	/**
	 * cron表达式，用于控制作业触发时间
	 * 
	 */
	@AliasFor("cron")
	String value() default "";

	/**
	 * cron表达式，用于控制作业触发时间
	 * 
	 */
	@AliasFor("value")
	String cron() default "";

	/**
	 * 作业名称
	 * 
	 */
	String jobName() default "";

	/**
	 * 作业分片总数
	 * 
	 */
	int shardingTotalCount() default 1;

	/**
	 * 分片序列号和参数用等号分隔，多个键值对用逗号分隔 分片序列号从0开始，不可大于或等于作业分片总数
	 *
	 * 如： 0=a,1=b,2=c
	 *
	 */
	String shardingItemParameters() default "";

	/**
	 * 作业自定义参数 作业自定义参数，可通过传递该参数为作业调度的业务方法传参，用于实现带参数的作业
	 *
	 * 例：每次获取的数据量、作业实例从数据库读取的主键等
	 *
	 */
	String jobParameter() default "";

	/**
	 * 作业描述信息
	 *
	 */
	String description() default "";

	/**
	 * 是否覆盖 Zookeeper 上的配置
	 *
	 */
	boolean overwrite() default true;

	/**
	 * 是否开启任务执行失效转移，开启表示如果作业在一次任务执行中途宕机，允许将该次未完成的任务在另一作业节点上补偿执行
	 *
	 */
	boolean failover() default false;

	/**
	 * 是否开启错过任务重新执行
	 *
	 */
	boolean misfire() default true;
}
