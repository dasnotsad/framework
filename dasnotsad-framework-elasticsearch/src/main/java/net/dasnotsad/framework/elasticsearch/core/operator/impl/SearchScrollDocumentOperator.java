package net.dasnotsad.framework.elasticsearch.core.operator.impl;

import net.dasnotsad.framework.elasticsearch.core.operator.IOperator;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class SearchScrollDocumentOperator implements IOperator<SearchScrollRequest, SearchResponse> {

    @Override
    public SearchResponse operator(RestHighLevelClient client, SearchScrollRequest request) throws IOException {
        return client.scroll(request, RequestOptions.DEFAULT);
    }
}
