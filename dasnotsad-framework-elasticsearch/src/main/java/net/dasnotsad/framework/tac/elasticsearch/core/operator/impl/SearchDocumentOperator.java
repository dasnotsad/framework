package net.dasnotsad.framework.tac.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class SearchDocumentOperator implements IOperator<SearchRequest, SearchResponse> {

    @Override
    public SearchResponse operator(RestHighLevelClient client, RequestOptions requestOptions, SearchRequest request) throws IOException {
        return client.search(request, requestOptions);
    }
}
