package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class ClearScrollOperator implements IOperator<ClearScrollRequest, ClearScrollResponse> {

    @Override
    public ClearScrollResponse operator(RestHighLevelClient client, RequestOptions requestOptions, ClearScrollRequest request) throws IOException {
        return client.clearScroll(request, requestOptions);
    }
}
