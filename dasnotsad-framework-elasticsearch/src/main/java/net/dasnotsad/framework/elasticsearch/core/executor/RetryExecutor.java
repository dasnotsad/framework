package net.dasnotsad.framework.elasticsearch.core.executor;

import java.io.IOException;
import java.util.Objects;

import net.dasnotsad.framework.elasticsearch.annotation.RestClientContainer;
import net.dasnotsad.framework.elasticsearch.core.operator.impl.*;
import net.dasnotsad.framework.elasticsearch.exception.EsException;
import org.elasticsearch.action.bulk.BulkResponse;

import lombok.extern.slf4j.Slf4j;
import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;

//操作执行器
@Slf4j
public class RetryExecutor implements IExector {

	private RestClientContainer containers;
	private ExecMonitor execMonitor;
	private String sysCode;
	private Long delayTime;
	private int retryNum;

	public RetryExecutor(String sysCode, RestClientContainer containers, Long delayTime) {
		this(5, sysCode, containers, delayTime);
	}

	public RetryExecutor(int retryNum, String sysCode, RestClientContainer containers, Long delayTime) {
		this.sysCode = sysCode;
		this.containers = containers;
		this.delayTime = delayTime;
		this.retryNum = retryNum;
	}

	@Override
	public <R, S> S exec(int whichDataSource, IOperator<R, S> operator, R request) {
		if (log.isDebugEnabled()) {
			execMonitor = new ExecMonitor();
			execMonitor.setStartTime(System.currentTimeMillis());
		}

		S response = null;
		try {

			// Query retry 5
			if (operator instanceof SearchDocumentOperator || operator instanceof SearchScrollDocumentOperator
					|| operator instanceof ClearScrollOperator || operator instanceof AliasesOperator
					|| operator instanceof IndicesExistsOperator || operator instanceof GetDocumentOperator
					|| operator instanceof MultiGetOperator || operator instanceof MultiSearchOperator) {
				loop: for (int count = 0; ++count <= this.retryNum;) {
					try {
						response = operator.operator(containers.get(whichDataSource), request);
						break loop;
					} catch (IOException e) {
						if(count < this.retryNum){
							log.error("查询操作发生异常，重试第" + count + "次...", e.getMessage(), e);
							Thread.sleep(1000L);
						}else{
							log.error("查询操作发生异常", e.getMessage(), e);
							throw new EsException("********************查询操作发生IO异常");
						}
					}
				}
			} else {
				response = operator.operator(containers.get(whichDataSource), request);
			}

			Objects.requireNonNull(response, "elasticsearch connection exception");

			if (response instanceof BulkResponse && ((BulkResponse) response).hasFailures()) {// 批处理返回
				BulkResponse bulkRes = (BulkResponse) response;
				log.warn("********************批处理操作存在失败：{}", bulkRes.buildFailureMessage());
			}
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("********************发生IO异常", e.getMessage(), e);
			throw new EsException("********************发生IO异常，已成功进入补偿服务");
		} catch (Exception e) {// 其它异常不做补偿
			e.printStackTrace();
			log.error(e.getMessage(), e);
			throw new EsException(e.getMessage());
		} finally {
			if (log.isDebugEnabled()) {
				execMonitor.setEndTime(System.currentTimeMillis());
				log.debug("本次耗时：" + execMonitor.getSpendTime() + "毫秒");
			}
		}
	}
}