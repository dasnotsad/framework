package net.dasnotsad.framework.tac.elasticsearch.core.executor;

public class ExecMonitor {
	private long startTime;
	private long endTime;

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getSpendTime() {
		return endTime - startTime;
	}
}