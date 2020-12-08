package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class BulkDocumentOperator implements IOperator<BulkRequest, BulkResponse> {

    @Override
    public BulkResponse operator(RestHighLevelClient client, RequestOptions requestOptions, BulkRequest request) throws IOException {
        return client.bulk(request, requestOptions);
    }
}
