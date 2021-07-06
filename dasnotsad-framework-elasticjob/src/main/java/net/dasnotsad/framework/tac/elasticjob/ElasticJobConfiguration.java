package net.dasnotsad.framework.tac.elasticjob;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuliwei
 * @create 2020-09-15
 */
@Configuration
public class ElasticJobConfiguration implements InitializingBean {

	@Autowired
	private ElasticJobProperties p;

	@Autowired
	private ApplicationContext context;

	@Value("${spring.application.name:my-app}")
	private String appName;

	public ZookeeperRegistryCenter zookeeperRegistryCenter() {
		ZookeeperConfiguration config = new ZookeeperConfiguration(p.getZookeeperNodes(), appName);
		config.setMaxRetries(p.getMaxRetries());
		config.setDigest(p.getZookeeperToken());
		config.setBaseSleepTimeMilliseconds(p.getBaseSleepTimeMilliseconds());
		config.setMaxSleepTimeMilliseconds(p.getMaxSleepTimeMilliseconds());
		config.setSessionTimeoutMilliseconds(p.getSessionTimeoutMilliseconds());
		config.setConnectionTimeoutMilliseconds(p.getConnectionTimeoutMilliseconds());

		ZookeeperRegistryCenter registry = new ZookeeperRegistryCenter(config);
		registry.init();
		return registry;
	}

	public ElasticJobListener elasticJobListener() {
		return new ElasticJobListener(p.getStartedTimeoutMilliseconds(), p.getCompletedTimeoutMilliseconds());
	}

	private ElasticJobService elasticJobService() {
		return new ElasticJobService(zookeeperRegistryCenter(), elasticJobListener());
	}

	private void init() {
		Map<String, SimpleJob> map = context.getBeansOfType(SimpleJob.class);
		for (Entry<String, SimpleJob> entry : map.entrySet()) {
			SimpleJob simpleJob = entry.getValue();
			ElasticJob annotation = simpleJob.getClass().getAnnotation(ElasticJob.class);
			if (annotation == null) {
				continue;
			}
			String cron = StringUtils.defaultIfBlank(annotation.cron(), annotation.value());
			if (StringUtils.isBlank(cron)) {
				continue;
			}
			String jobName = StringUtils.defaultIfBlank(annotation.jobName(), simpleJob.getClass().getName());
			elasticJobService().addJob(simpleJob, jobName, cron, annotation.shardingTotalCount(),
					annotation.misfire(), annotation.failover(),
					annotation.overwrite(), annotation.description(), annotation.jobParameter(),
					annotation.shardingItemParameters());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Objects.requireNonNull(p.getZookeeperNodes(), "dasnotsad.paas.elasticjob.zookeeper-nodes must be setting");
		init();
	}

}
