package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class SearchDocumentOperator implements IOperator<SearchRequest, SearchResponse> {

    @Override
    public SearchResponse operator(RestHighLevelClient client, SearchRequest request) throws IOException {
        return client.search(request, RequestOptions.DEFAULT);
    }
}
