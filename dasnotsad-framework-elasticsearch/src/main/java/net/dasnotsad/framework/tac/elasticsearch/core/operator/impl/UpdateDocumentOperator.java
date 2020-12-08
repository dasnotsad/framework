package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class UpdateDocumentOperator implements IOperator<UpdateRequest, UpdateResponse> {

    @Override
    public UpdateResponse operator(RestHighLevelClient client, RequestOptions requestOptions, UpdateRequest request) throws IOException {
        return client.update(request, requestOptions);
    }
}
