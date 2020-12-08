package net.dasnotsad.framework.tac.elasticsearch.core.executor;

import java.io.IOException;
import java.util.Objects;

import net.dasnotsad.framework.tac.elasticsearch.annotation.RestClientContainer;
import net.dasnotsad.framework.tac.elasticsearch.core.operator.impl.*;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import net.dasnotsad.framework.tac.elasticsearch.exception.EsException;
import net.dasnotsad.framework.tac.elasticsearch.retreat.feign.func.DelayMessageBuilder;

//操作执行器
@Slf4j
public class RetryExecutor implements IExector {

	private RestClientContainer containers;
	private ExecMonitor execMonitor;
	private DelayMessageBuilder delayMessageBuilder;
	private String sysCode;
	private Long delayTime;
	private int retryNum;

	public RetryExecutor(String sysCode, RestClientContainer containers, Long delayTime,
						 DelayMessageBuilder delayMessageBuilder) {
		this(5, sysCode, containers, delayTime, delayMessageBuilder);
	}

	public RetryExecutor(int retryNum, String sysCode, RestClientContainer containers, Long delayTime,
						 DelayMessageBuilder delayMessageBuilder) {
		this.sysCode = sysCode;
		this.containers = containers;
		this.delayTime = delayTime;
		this.delayMessageBuilder = delayMessageBuilder;
		this.retryNum = retryNum;
	}

	@Override
	public <R, S> S exec(int whichDataSource, IOperator<R, S> operator, R request, RequestOptions requestOptions) {
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
						response = operator.operator(containers.get(whichDataSource), requestOptions, request);
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
				response = operator.operator(containers.get(whichDataSource), requestOptions, request);
			}

			Objects.requireNonNull(response, "elasticsearch connection exception");

			if (response instanceof DocWriteResponse) {// 写操作判断
				String desc = checkResponse((DocWriteResponse) response);// 验证是否需要补偿
				if (!StringUtils.isEmpty(desc)) {// 需要进行补偿
					log.error("********************判断需要进行补偿，将进入补偿逻辑...{}", desc);
					/*if (request instanceof IndexRequest)
						delayMessageBuilder.createRetreatRecord(whichDataSource, (IndexRequest) request, sysCode,
								OperationType.INSERT, delayTime, desc);
					if (request instanceof UpdateRequest)
						delayMessageBuilder.createRetreatRecord(whichDataSource, (UpdateRequest) request, sysCode,
								OperationType.UPDATE, delayTime, desc);
					if (request instanceof DeleteRequest)
						delayMessageBuilder.createRetreatRecord(whichDataSource, (DeleteRequest) request, sysCode,
								OperationType.DELETE, delayTime, desc);*/
				}
			}
			if (response instanceof BulkResponse && ((BulkResponse) response).hasFailures()) {// 批处理返回
				BulkResponse bulkRes = (BulkResponse) response;
				log.warn("********************批处理操作存在失败：{}", bulkRes.buildFailureMessage());
			}
			return response;
		} catch (IOException e) {
			log.error("********************发生IO异常", e.getMessage(), e);
			if (request instanceof DocWriteRequest || request instanceof WriteRequest) {
				log.error("********************判断为写操作，将进入补偿逻辑...");
				/*if (request instanceof IndexRequest)
					delayMessageBuilder.createRetreatRecord(whichDataSource, (IndexRequest) request, sysCode,
							OperationType.INSERT, delayTime, e.getMessage());
				if (request instanceof UpdateRequest)
					delayMessageBuilder.createRetreatRecord(whichDataSource, (UpdateRequest) request, sysCode,
							OperationType.UPDATE, delayTime, e.getMessage());
				if (request instanceof DeleteRequest)
					delayMessageBuilder.createRetreatRecord(whichDataSource, (DeleteRequest) request, sysCode,
							OperationType.DELETE, delayTime, e.getMessage());
				if (request instanceof BulkRequest)
					delayMessageBuilder.createRetreatRecord(whichDataSource, (BulkRequest) request, sysCode,
							OperationType.BULK, delayTime, e.getMessage());*/
			}
			throw new EsException("********************发生IO异常，已成功进入补偿服务");
		} catch (Exception e) {// 其它异常不做补偿
			log.error(e.getMessage(), e);
			throw new EsException(e.getMessage());
		} finally {
			if (log.isDebugEnabled()) {
				execMonitor.setEndTime(System.currentTimeMillis());
				log.debug("本次耗时：" + execMonitor.getSpendTime() + "毫秒");
			}
		}
	}

	// 判断回调是否需要补偿
	private String checkResponse(DocWriteResponse response) {
		switch (response.status()) {
		case NOT_FOUND:
			log.error("********************RestStatus.NOT_FOUND检测到404异常，将进入补偿逻辑...");
			return "RestStatus.NOT_FOUND（404异常）";
		case REQUEST_TIMEOUT:
			log.error("********************RestStatus.REQUEST_TIMEOUT检测到请求超时，将进入补偿逻辑...");
			return "RestStatus.REQUEST_TIMEOUT（到请求超时）";
		case BAD_GATEWAY:
			log.error("********************RestStatus.BAD_GATEWAY检测到网关错误，将进入补偿逻辑...");
			return "RestStatus.BAD_GATEWAY（网关错误）";
		case SERVICE_UNAVAILABLE:
			log.error("********************RestStatus.SERVICE_UNAVAILABLE检测到服务暂不可用，将进入补偿逻辑...");
			return "RestStatus.SERVICE_UNAVAILABLE（服务暂不可用）";
		case GATEWAY_TIMEOUT:
			log.error("********************检测到网关超时，将进入补偿逻辑...");
			return "RestStatus.GATEWAY_TIMEOUT（网关超时）";
		case INSUFFICIENT_STORAGE:
			log.error("********************检测到磁盘空间不足，将进入补偿逻辑...");
			return "RestStatus.INSUFFICIENT_STORAGE（磁盘空间不足）";
		default:
			return null;
		}
	}
}