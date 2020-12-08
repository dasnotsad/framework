package net.dasnotsad.framework.tac.elasticsearch.repository;

import net.dasnotsad.framework.tac.elasticsearch.metadata.MetaData;
import org.elasticsearch.index.reindex.BulkByScrollResponse;

import java.io.Serializable;
import java.util.Map;

/**
 * @Description: TODO
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public interface ElasticsearchRepository<T, ID extends Serializable> extends ElasticSearchCrudRepository<T, ID> {

    Class<T> getEntityClass();

    MetaData getMetaData();

    String[] getSearchIndexNames();

    void setIndexName(String indexName);

    String getIndexName();

    BulkByScrollResponse queryUpdate(String indexName, Map<String, Object> terms, Map<String, Object> updates);

    BulkByScrollResponse queryDelete(String indexName, Map<String, Object> terms);
}
