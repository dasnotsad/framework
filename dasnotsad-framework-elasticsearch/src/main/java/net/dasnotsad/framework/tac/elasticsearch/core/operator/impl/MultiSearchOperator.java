package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class MultiSearchOperator implements IOperator<MultiSearchRequest, MultiSearchResponse> {

    @Override
    public MultiSearchResponse operator(RestHighLevelClient client, RequestOptions requestOptions, MultiSearchRequest request) throws IOException {
        return client.msearch(request, requestOptions);
    }
}
