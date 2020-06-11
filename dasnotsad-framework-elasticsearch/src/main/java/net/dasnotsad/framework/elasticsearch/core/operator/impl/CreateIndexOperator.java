package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class CreateIndexOperator implements IOperator<CreateIndexRequest, CreateIndexResponse> {

    @Override
    public CreateIndexResponse operator(RestHighLevelClient client, CreateIndexRequest request) throws IOException {
        return client.indices().create(request, RequestOptions.DEFAULT);
    }
}
