package net.dasnotsad.framework.tac.elasticjob;

import static java.util.Objects.requireNonNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

/**
 * @author liuliwei
 * @create 2020-09-15
 */
public class ElasticJobService implements InitializingBean {

	private ZookeeperRegistryCenter zookeeperRegistryCenter;
	private ApplicationContext context;
	private ElasticJobListener elasticJobListener;

	public ElasticJobService(ZookeeperRegistryCenter zookeeperRegistryCenter, ElasticJobListener elasticJobListener,
							 ApplicationContext context) {
		this.zookeeperRegistryCenter = zookeeperRegistryCenter;
		this.elasticJobListener = elasticJobListener;
		this.context = context;
	}

	/**
	 * 添加定时任务
	 *
	 */
	public <T extends SimpleJob> void addJob(T simpleJob, String jobName, String cron, int shardingTotalCount,
											 String zookeeperNodes, String zookeeperNamespace,
											boolean misfire, boolean failover, boolean overwrite, String description,
											 String jobParameter, String shardingItemParameters) {
		requireNonNull(simpleJob);
		jobName = StringUtils.defaultIfBlank(jobName, simpleJob.getClass().getName());
		JobConfiguration jobConfig = JobConfiguration.newBuilder(jobName, shardingTotalCount)
				.cron(cron)
				.misfire(misfire)
				.failover(failover)
				.overwrite(overwrite)
				.description(description)
				.jobParameter(jobParameter)
				.shardingItemParameters(shardingItemParameters)
				.build();
		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(zookeeperNodes, zookeeperNamespace));
		regCenter.init();
		new ScheduleJobBootstrap(regCenter, simpleJob, jobConfig)
				.schedule();
	}

	@Override
	public void afterPropertiesSet() {
		requireNonNull(this.zookeeperRegistryCenter);
		requireNonNull(this.elasticJobListener);
		requireNonNull(this.context);
	}

}
