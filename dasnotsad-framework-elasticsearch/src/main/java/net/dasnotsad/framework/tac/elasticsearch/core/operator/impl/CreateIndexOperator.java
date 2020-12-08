package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class CreateIndexOperator implements IOperator<CreateIndexRequest, CreateIndexResponse> {

    @Override
    public CreateIndexResponse operator(RestHighLevelClient client, RequestOptions requestOptions, CreateIndexRequest request) throws IOException {
        return client.indices().create(request, requestOptions);
    }
}
