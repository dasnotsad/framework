package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class InsertDocumentOperator implements IOperator<IndexRequest, IndexResponse> {

    @Override
    public IndexResponse operator(RestHighLevelClient client, IndexRequest request) throws IOException {
        return client.index(request, RequestOptions.DEFAULT);
    }
}
