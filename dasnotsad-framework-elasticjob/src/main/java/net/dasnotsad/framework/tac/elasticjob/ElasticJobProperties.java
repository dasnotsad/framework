package net.dasnotsad.framework.tac.elasticjob;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liuliwei
 * @create 2020-09-15
 */
@ConfigurationProperties(prefix = "dasnotsad.paas.elasticjob")
public class ElasticJobProperties {

	/**
	 * 连接Zookeeper服务器的列表 包括IP地址和端口号 多个地址用逗号分隔 如: host1:2181,host2:2181
	 */
	private String zookeeperNodes;

	/**
	 * Zookeeper的命名空间
	 */
	private String zookeeperNamespace;

	/**
	 * 连接Zookeeper的权限令牌 缺省为不需要权限验证
	 */
	private String zookeeperToken;

	/**
	 * 会话超时时间 单位：毫秒
	 */
	private int sessionTimeoutMilliseconds = 60000;

	/**
	 * 连接超时时间 单位：毫秒
	 */
	private int connectionTimeoutMilliseconds = 15000;

	/**
	 * 等待重试的间隔时间的初始值 单位：毫秒
	 */
	private int baseSleepTimeMilliseconds = 3000;

	/**
	 * 等待重试的间隔时间的最大值 单位：毫秒
	 */
	private int maxSleepTimeMilliseconds = 3000;

	/**
	 * 最大重试次数
	 */
	private int maxRetries = 10;

	private long startedTimeoutMilliseconds = 30000;

	private long completedTimeoutMilliseconds = 30000;

	public String getZookeeperNodes() {
		return zookeeperNodes;
	}

	public void setZookeeperNodes(String zookeeperNodes) {
		this.zookeeperNodes = zookeeperNodes;
	}

	public String getZookeeperNamespace() {
		return zookeeperNamespace;
	}

	public void setZookeeperNamespace(String zookeeperNamespace) {
		this.zookeeperNamespace = zookeeperNamespace;
	}

	public String getZookeeperToken() {
		return zookeeperToken;
	}

	public void setZookeeperToken(String zookeeperToken) {
		this.zookeeperToken = zookeeperToken;
	}

	public int getSessionTimeoutMilliseconds() {
		return sessionTimeoutMilliseconds;
	}

	public void setSessionTimeoutMilliseconds(int sessionTimeoutMilliseconds) {
		this.sessionTimeoutMilliseconds = sessionTimeoutMilliseconds;
	}

	public int getConnectionTimeoutMilliseconds() {
		return connectionTimeoutMilliseconds;
	}

	public void setConnectionTimeoutMilliseconds(int connectionTimeoutMilliseconds) {
		this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
	}

	public int getBaseSleepTimeMilliseconds() {
		return baseSleepTimeMilliseconds;
	}

	public void setBaseSleepTimeMilliseconds(int baseSleepTimeMilliseconds) {
		this.baseSleepTimeMilliseconds = baseSleepTimeMilliseconds;
	}

	public int getMaxSleepTimeMilliseconds() {
		return maxSleepTimeMilliseconds;
	}

	public void setMaxSleepTimeMilliseconds(int maxSleepTimeMilliseconds) {
		this.maxSleepTimeMilliseconds = maxSleepTimeMilliseconds;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public long getStartedTimeoutMilliseconds() {
		return startedTimeoutMilliseconds;
	}

	public void setStartedTimeoutMilliseconds(long startedTimeoutMilliseconds) {
		this.startedTimeoutMilliseconds = startedTimeoutMilliseconds;
	}

	public long getCompletedTimeoutMilliseconds() {
		return completedTimeoutMilliseconds;
	}

	public void setCompletedTimeoutMilliseconds(long completedTimeoutMilliseconds) {
		this.completedTimeoutMilliseconds = completedTimeoutMilliseconds;
	}

}
