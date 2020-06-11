package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class AliasesOperator implements IOperator<GetAliasesRequest, GetAliasesResponse> {

    @Override
    public GetAliasesResponse operator(RestHighLevelClient client, GetAliasesRequest request) throws IOException {
        return client.indices().getAlias(request, RequestOptions.DEFAULT);
    }
}
