package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import java.io.IOException;

public class QueryDeleteOperator implements IOperator<DeleteByQueryRequest, BulkByScrollResponse> {
    @Override
    public BulkByScrollResponse operator(RestHighLevelClient client, RequestOptions requestOptions, DeleteByQueryRequest request) throws IOException {
        return client.deleteByQuery(request, requestOptions);
    }
}
