package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class IndicesExistsOperator implements IOperator<GetIndexRequest, Boolean> {

    @Override
    public Boolean operator(RestHighLevelClient client, RequestOptions requestOptions, GetIndexRequest request) throws IOException {
        return client.indices().exists(request, requestOptions);
    }
}
