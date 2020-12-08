package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class GetDocumentOperator implements IOperator<GetRequest, GetResponse> {

    @Override
    public GetResponse operator(RestHighLevelClient client, RequestOptions requestOptions, GetRequest request) throws IOException {
        return client.get(request, requestOptions);
    }
}
