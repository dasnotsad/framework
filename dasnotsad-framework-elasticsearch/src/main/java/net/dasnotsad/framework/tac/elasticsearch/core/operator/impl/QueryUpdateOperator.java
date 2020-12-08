package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;

import java.io.IOException;

public class QueryUpdateOperator implements IOperator<UpdateByQueryRequest, BulkByScrollResponse> {

    @Override
    public BulkByScrollResponse operator(RestHighLevelClient client, RequestOptions requestOptions, UpdateByQueryRequest request) throws IOException {
        return client.updateByQuery(request, requestOptions);
    }
}
