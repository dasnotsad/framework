package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class DeleteIndexOperator implements IOperator<DeleteIndexRequest, AcknowledgedResponse> {

    @Override
    public AcknowledgedResponse operator(RestHighLevelClient client, DeleteIndexRequest request) throws IOException {
        return client.indices().delete(request, RequestOptions.DEFAULT);
    }
}
