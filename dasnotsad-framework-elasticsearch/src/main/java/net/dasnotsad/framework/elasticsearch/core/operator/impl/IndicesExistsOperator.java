package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class IndicesExistsOperator implements IOperator<GetIndexRequest, Boolean> {

    @Override
    public Boolean operator(RestHighLevelClient client, GetIndexRequest request) throws IOException {
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }
}
