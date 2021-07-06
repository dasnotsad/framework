package net.dasnotsad.framework.tac.elasticjob;

import static java.util.Objects.requireNonNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author liuliwei
 * @create 2020-09-15
 */
public class ElasticJobService implements InitializingBean {

	private ZookeeperRegistryCenter zookeeperRegistryCenter;
	private ElasticJobListener elasticJobListener;

	public ElasticJobService(ZookeeperRegistryCenter zookeeperRegistryCenter, ElasticJobListener elasticJobListener) {
		this.zookeeperRegistryCenter = zookeeperRegistryCenter;
		this.elasticJobListener = elasticJobListener;
	}

	/**
	 * 添加定时任务
	 *
	 */
	public <T extends SimpleJob> void addJob(T simpleJob, String jobName, String cron, int shardingTotalCount,
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
		new ScheduleJobBootstrap(zookeeperRegistryCenter, simpleJob, jobConfig)
				.schedule();
	}

	@Override
	public void afterPropertiesSet() {
		requireNonNull(this.zookeeperRegistryCenter);
		requireNonNull(this.elasticJobListener);
	}

}
