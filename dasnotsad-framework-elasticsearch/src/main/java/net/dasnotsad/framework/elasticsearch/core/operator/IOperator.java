package net.dasnotsad.framework.elasticsearch.core.operator;

import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

//ES操作统一执行器
public interface IOperator<R, S> {

	S operator(RestHighLevelClient client, R request) throws IOException;
}
