package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class DeleteIndexOperator implements IOperator<DeleteIndexRequest, AcknowledgedResponse> {

    @Override
    public AcknowledgedResponse operator(RestHighLevelClient client, RequestOptions requestOptions, DeleteIndexRequest request) throws IOException {
        return client.indices().delete(request, requestOptions);
    }
}
