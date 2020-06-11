package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class DeleteDocumentOperator implements IOperator<DeleteRequest, DeleteResponse> {

    @Override
    public DeleteResponse operator(RestHighLevelClient client, DeleteRequest request) throws IOException {
        return client.delete(request, RequestOptions.DEFAULT);
    }
}
