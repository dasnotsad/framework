package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class MultiGetOperator implements IOperator<MultiGetRequest, MultiGetResponse> {

    @Override
    public MultiGetResponse operator(RestHighLevelClient client, MultiGetRequest request) throws IOException {
        return client.mget(request, RequestOptions.DEFAULT);
    }
}
