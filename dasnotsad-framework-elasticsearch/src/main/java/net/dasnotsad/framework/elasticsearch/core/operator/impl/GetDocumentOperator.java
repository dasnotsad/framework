package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class GetDocumentOperator implements IOperator<GetRequest, GetResponse> {

    @Override
    public GetResponse operator(RestHighLevelClient client, GetRequest request) throws IOException {
        return client.get(request, RequestOptions.DEFAULT);
    }
}
