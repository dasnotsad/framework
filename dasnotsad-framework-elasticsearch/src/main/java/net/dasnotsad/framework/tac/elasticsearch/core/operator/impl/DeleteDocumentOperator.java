package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class DeleteDocumentOperator implements IOperator<DeleteRequest, DeleteResponse> {

    @Override
    public DeleteResponse operator(RestHighLevelClient client, RequestOptions requestOptions, DeleteRequest request) throws IOException {
        return client.delete(request, requestOptions);
    }
}
