package net.dasnotsad.framework.tac.elasticjob;

import org.apache.shardingsphere.elasticjob.infra.listener.ShardingContexts;
import org.apache.shardingsphere.elasticjob.lite.api.listener.AbstractDistributeOnceElasticJobListener;

/**
 * @author liuliwei
 * @create 2020-09-15
 */
public class ElasticJobListener extends AbstractDistributeOnceElasticJobListener {

	public ElasticJobListener(final long startedTimeoutMilliseconds, final long completedTimeoutMilliseconds) {
		super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
	}

	@Override
	public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {

	}

	@Override
	public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {

	}

	@Override
	public String getType() {
		return null;
	}

}