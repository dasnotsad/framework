package net.dasnotsad.framework.tac.elasticsearch;

import com.alibaba.fastjson.JSONArray;
import lombok.SneakyThrows;
import net.dasnotsad.framework.tac.elasticsearch.utils.IndexToolkit;
import net.dasnotsad.framework.tac.elasticsearch.utils.MappingToolkit;
import net.dasnotsad.framework.tac.data.metadata.Pageable;
import net.dasnotsad.framework.tac.elasticsearch.core.page.PageResult;
import net.dasnotsad.framework.tac.elasticsearch.exception.EsException;
import net.dasnotsad.framework.tac.elasticsearch.metadata.MetaData;
import net.dasnotsad.framework.tac.elasticsearch.repository.ElasticsearchRepository;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @Description: TODO
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public abstract class AbstractElasticSearchRepository<T, ID extends Serializable> implements ElasticsearchRepository<T, ID> {

    @Lazy
    @Resource
    private ESTemplate eSTemplate;

    public static int DEFAULT_DATASOURCE = 0;

    private int whichDataSource = 0;

    protected Class<T> entityClass;

    protected MetaData metaData;

    /**
     * 判断索引是否存在
     *
     * @return boolean
     */
    @Override
    public boolean indexExists() {
        String indexName = this.getIndexName();
        return this.indexExists(indexName);
    }

    @Override
    public boolean indexExists(String index) {
        return this.getESTemplate().indexExists(index);
    }

    /**
     * 创建索引
     *
     * @return CreateIndexResponse
     */
    @SneakyThrows
    @Override
    public CreateIndexResponse createIndex() {
        Class<T> entityClass = this.getEntityClass();
        MetaData metaData = this.getMetaData();
        String indexName = metaData.getIndexName();
        int shards = metaData.getShards();
        int replicas = metaData.getReplicas();
        XContentBuilder mapping = MappingToolkit.createMapping(entityClass);
        if(mapping != null)
            return this.getESTemplate().createIndex(indexName, shards, replicas, mapping);
        else
            return this.getESTemplate().createIndex(indexName, shards, replicas);
    }

    /**
     * 删除索引
     *
     * @return
     */
    @Override
    public AcknowledgedResponse deleteIndex() {
        String[] searchIndexNames = this.getSearchIndexNames();
        return this.deleteIndex(searchIndexNames);
    }

    /**
     * 删除索引
     *
     * @param indexs 索引名数组
     * @return
     */
    @Override
    public AcknowledgedResponse deleteIndex(String... indexs) {
        Assert.notNull(indexs, "indexs can't be null.");
        return this.getESTemplate().deleteIndex(indexs);
    }

    /**
     * 手动设置indexName，以满足动态按月存储等需求
     *
     * @param indexName 索引名数组
     */
    @Override
    public void setIndexName(String indexName) {
        getMetaData().setIndexName(indexName==null ? getMetaData().getIndexName() : indexName);
    }

    /**
     * 根据id删除
     *
     * @param id id值
     * @return
     */
    @Override
    public void deleteById(ID id) {
        Assert.notNull(id, "id can't be null.");
        String indexName = this.getIndexName();
        DeleteResponse deleteResponse = this.getESTemplate().deleteById(indexName, this.stringIdRepresentation(id));
    }

    /**
     * 批量删除
     *
     * @param ids id列表
     * @return
     */
    @Override
    public void deleteBatchIds(Iterable<ID> ids) {
        Assert.notNull(ids, "ids can't be null.");
        String indexName = this.getIndexName();
        BulkResponse bulkItemResponses = this.getESTemplate().bulkDelete(indexName, this.stringIdsRepresentation(ids));
    }

    /**
     * 删除指定索引下的所有数据
     */
    @Override
    public void deleteAll() {
        String[] indexName = getSearchIndexNames();
        this.deleteIndex(indexName);
        this.createIndex();
    }

    /**
     * 批量删除
     *
     * @param entitys 实体列表
     * @return
     */
    @SneakyThrows
    @Override
    public void deleteBatch(Collection<? extends T> entitys) {
        Assert.notNull(entitys, "Cannot deleteBatch 'entitys' entity.");
        Assert.notEmpty(entitys, "Cannot deleteBatch empty List.");
        List<String> ids = new ArrayList();
        for (T entity : entitys) {
            ids.add(IndexToolkit.getESId(entity));
        }
        String indexName = this.getIndexName();

        BulkResponse bulkItemResponses = this.getESTemplate().bulkDelete(indexName, ids);
    }

    /**
     * 按条件删除
     *
     * @param indexName 索引名称
     * @param terms     需要匹配的条件Map
     * @return
     */
    @Override
    public BulkByScrollResponse queryDelete(String indexName, Map<String, Object> terms){
        Assert.notNull(indexName, "indexName cannot be null.");
        Assert.notNull(terms, "terms cannot be null.");
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
        //遇到版本冲突将会记录进failures，而非中止
        request.setConflicts("proceed");
        BoolQueryBuilder termsBuilder = QueryBuilders.boolQuery();
        terms.forEach((k, v) -> termsBuilder.must(new TermQueryBuilder(k, v)));
        request.setQuery(termsBuilder);
        return this.getESTemplate().queryDelete(request);
    }

    /**
     * TODO 插入文档
     *
     * @param entity 数据对象
     * @return
     */
    @SneakyThrows
    @Override
    public <S extends T> S insert(S entity) {
        Assert.notNull(entity, "entity can't be null.");
        MetaData metaData = this.getMetaData();
        String indexName = metaData.getIndexName();
        String esId = IndexToolkit.getESId(entity);

        if (metaData.isCreateIndex() && !indexExists(indexName)) {
            createIndex();
        }
        try {
            IndexResponse indexResponse = this.getESTemplate().insertDocment(indexName, esId, entity);
        } catch (Exception e) {
            throw new EsException(e);
        }
        return entity;
    }

    /**
     * TODO 批量插入文档
     *
     * @param entitys 数据对象列表
     * @return
     */
    @Override
    public Collection<? extends T> insertBatch(Collection<? extends T> entitys) {
        Assert.notNull(entitys, "entitys can't be null.");
        Assert.notEmpty(entitys, "Cannot insertBatch empty List.");
        MetaData metaData = this.getMetaData();
        String indexName = metaData.getIndexName();
        if (metaData.isCreateIndex() && !indexExists(indexName)) {
            createIndex();
        }
        List<? extends T> objects = new ArrayList(entitys);
        BulkResponse bulkResponse = this.getESTemplate().bulkInsert(indexName, objects);
        return entitys;
    }

    /**
     * 异步写入
     *
     * @param entity 存储对象
     */
    @Override
    public <S extends T> void insertAsync(S entity) {
        Assert.notNull(entity, "entity can't be null.");
        MetaData metaData = this.getMetaData();
        String indexName = metaData.getIndexName();
        if (metaData.isCreateIndex() && !indexExists(indexName)) {
            createIndex();
        }
        this.getESTemplate().bulkAsyncInsert(indexName, entity);
    }

    /**
     * 批量异步写入
     *
     * @param entitys 存储对象列表
     */
    @Override
    public void insertBatchAsync(Collection<? extends T> entitys) {
        Assert.notNull(entitys, "entitys can't be null.");
        Assert.notEmpty(entitys, "Cannot insertBatchAsync empty List.");
        MetaData metaData = this.getMetaData();
        String indexName = metaData.getIndexName();
        if (metaData.isCreateIndex() && !indexExists(indexName)) {
            createIndex();
        }
        this.getESTemplate().bulkAsyncInsert(indexName, entitys);
    }

    /**
     * 根据id更新
     *
     * @param entity 数据对象
     * @return
     */
    @SneakyThrows
    @Override
    public <S extends T> void updateById(S entity) {
        Assert.notNull(entity, "entity can't be null.");
        String esId = IndexToolkit.getESId(entity);
        String indexName = this.getIndexName();
        UpdateResponse updateResponse = this.getESTemplate().updateById(indexName, esId, entity);
    }

    /**
     * 批量修改
     *
     * @param entitys 数据对象列表
     * @return
     */
    @Override
    public void updateBatch(Collection<? extends T> entitys) {
        Assert.notNull(entitys, "Cannot updateBatch 'entitys' entity.");
        Assert.notEmpty(entitys, "Cannot updateBatch empty List.");
        List<? extends T> list = new ArrayList(entitys);
        String indexName = this.getIndexName();
        BulkResponse bulkItemResponses = this.getESTemplate().bulkUpdate(indexName, list);
    }

    /**
     * 按条件修改
     *
     * @param indexName 索引名称
     * @param terms     需要匹配的条件Map
     * @param updates   需要修改的字段Map
     * @return
     */
    @Override
    public BulkByScrollResponse queryUpdate(String indexName, Map<String, Object> terms, Map<String, Object> updates){
        Assert.notNull(indexName, "indexName cannot be null.");
        Assert.notNull(terms, "terms cannot be null.");
        Assert.notNull(updates, "updates cannot be null.");
        UpdateByQueryRequest request = new UpdateByQueryRequest(indexName);
        BoolQueryBuilder termsBuilder = QueryBuilders.boolQuery();
        terms.forEach((k, v) -> termsBuilder.must(new TermQueryBuilder(k, v)));
        request.setQuery(termsBuilder);
        StringBuilder script = new StringBuilder();
        Set<String> keys = updates.keySet();
        for (String key : keys) {
            String appendValue = "";
            Object value = updates.get(key);
            if (value instanceof Number) {
                appendValue = value.toString();
            } else if (value instanceof String) {
                appendValue = "'" + value.toString() + "'";
            } else if (value instanceof Collection){
                appendValue = JSONArray.toJSONString(value);
            } else {
                appendValue = value.toString();
            }
            script.append("ctx._source.").append(key).append("=").append(appendValue).append(";");
        }
        request.setScript(new Script(script.toString()));
        return this.getESTemplate().queryUpdate(request);
    }

    @Override
    public void deleteAttrs(T bean, ID id, List<String> keys) {
        Assert.notNull(id, "id can't be null.");
        this.deleteAttrs(bean, Arrays.asList(id), keys);
    }

    @Override
    public void deleteAttrs(T bean, List<ID> ids, List<String> keys) {
        Assert.notNull(keys, "keys can't be null.");
        Assert.notNull(bean, "bean can't be null.");
        String indexName = this.getIndexName();
        this.getESTemplate().deleteAttrsByParams(indexName, this.stringIdsRepresentation(ids), keys, getWhichDataSource());
    }

    /**
     * 批量get查询
     *
     * @param request 查询请求
     * @return
     */
    @Override
    public MultiGetResponse getBatch(MultiGetRequest request) {
        Assert.notNull(request, "request can't be null.");
        return this.getESTemplate().multiGet(request);
    }

    /**
     * 批量search
     *
     * @param request 查询请求
     * @return
     */
    @Override
    public MultiSearchResponse searchBatch(MultiSearchRequest request) {
        Assert.notNull(request, "request can't be null.");
        return this.getESTemplate().multiSearch(request);
    }

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    @Override
    public T selectById(ID id) {
        Assert.notNull(id, "id can't be null.");
        Class<T> entityClass = this.getEntityClass();
        MetaData metaData = this.getMetaData(entityClass);
        String indexName = metaData.getIndexName();
        return this.getESTemplate().getSingleById(indexName, stringIdRepresentation(id), entityClass);
    }

    /**
     * 根据ids查询
     *
     * @param ids id列表
     * @return
     */
    @Override
    public Iterable<T> selectBatchIds(Iterable<ID> ids) {
        Assert.notNull(ids, "ids can't be null.");
        Class<T> entityClass = this.getEntityClass();
        MetaData metaData = this.getMetaData(entityClass);
        String[] searchIndex = metaData.getSearchIndexNames();
        ESTemplate eSTemplate = this.getESTemplate();
        return eSTemplate.retrieveDocumentByIds(stringIdsRepresentation(ids), entityClass, searchIndex);
    }

    /**
     * 批量查询
     *
     * @return
     */
    @Override
    public Iterable<T> selectBatch() {
        return this.selectBatch(new SearchSourceBuilder());
    }

    /**
     * 批量查询
     *
     * @param sourceBuilder 查询语句对象
     * @return
     */
    @Override
    public Iterable<T> selectBatch(SearchSourceBuilder sourceBuilder) {
        Class<T> entityClass = this.getEntityClass();
        MetaData metaData = this.getMetaData(entityClass);
        String[] searchIndex = metaData.getSearchIndexNames();
        return this.getESTemplate().retrieveDocument(sourceBuilder, entityClass, searchIndex);
    }


    /**
     * 根据ids查询
     *
     * @param ids   id列表
     * @param sorts 排序
     * @return
     */
    @Override
    public Iterable<T> selectBatchByIds(Iterable<ID> ids, List<SortBuilder<?>> sorts) {
        Assert.notNull(ids, "ids can't be null.");

        Assert.notNull(sorts, "Cannot queryByIds 'sorts' entity.");
        Assert.notEmpty(sorts, "Cannot queryByIds empty List.");

        Class<T> entityClass = this.getEntityClass();
        MetaData metaData = this.getMetaData(entityClass);
        String[] searchIndex = metaData.getSearchIndexNames();

        return this.getESTemplate().queryByIds(stringIdsRepresentation(ids), entityClass, sorts, searchIndex);
    }

    protected SearchResponse query(final SearchSourceBuilder sourceBuilder) {
        MetaData metaData = this.getMetaData();
        String[] searchIndex = metaData.getSearchIndexNames();
        return this.getESTemplate().query(getWhichDataSource(), sourceBuilder, searchIndex);
    }

    /**
     * scroll查询
     *
     * @param sourceBuilder 查询语句对象
     * @param keepAlive     scroll快照有效时间（1m代表1分钟）
     * @return
     */
    @Override
    public SearchResponse selectScroll(SearchSourceBuilder sourceBuilder, String keepAlive) {
        Assert.notNull(sourceBuilder, "sourceBuilder can't be null.");
        Assert.notNull(keepAlive, "keepAlive can't be null.");

        MetaData metaData = this.getMetaData();
        String[] searchIndex = metaData.getSearchIndexNames();

        return this.getESTemplate().queryScroll(sourceBuilder, keepAlive, searchIndex);
    }

    /**
     * scroll查询
     *
     * @param scrollId  scrollId值
     * @param keepAlive scroll快照有效时间（1m代表1分钟）
     * @return
     */
    @Override
    public SearchResponse selectScroll(String scrollId, String keepAlive) {
        Assert.notNull(scrollId, "scrollId can't be null.");
        Assert.notNull(keepAlive, "keepAlive can't be null.");
        return this.getESTemplate().queryScroll(scrollId, keepAlive);
    }


    @Override
    public Pageable<T> selectPage(Pageable pageable) {
        Assert.notNull(pageable, "pageable can't be null.");
        Class<T> entityClass = this.getEntityClass();
        MetaData metaData = this.getMetaData(entityClass);
        String[] searchIndex = metaData.getSearchIndexNames();

        int currentPage = pageable.getCurrentPage();
        int pageSize = pageable.getPageSize();
        ESTemplate esTemplate = this.getESTemplate();
        PageResult<T> pageResult = esTemplate.searchScrollDeepPaging(new SearchSourceBuilder(), currentPage, pageSize, entityClass, searchIndex);
        return Pageable.build(pageResult.getPageNumber(), pageResult.getPageSize(), pageResult.getTotal(), pageResult.getList());
    }

    /**
     * 深度分页查询
     *
     * @param currentPage         页码
     * @param pageSize            每页条数
     * @param searchSourceBuilder 查询语句对象
     * @return
     */
    @Override
    public Pageable<T> selectPage(int currentPage, int pageSize, SearchSourceBuilder searchSourceBuilder) {
        Assert.notNull(searchSourceBuilder, "searchSourceBuilder can't be null.");

        Class<T> entityClass = this.getEntityClass();
        MetaData metaData = this.getMetaData(entityClass);
        String[] searchIndex = metaData.getSearchIndexNames();

        ESTemplate esTemplate = this.getESTemplate();
        PageResult<T> pageResult = esTemplate.searchScrollDeepPaging(searchSourceBuilder, currentPage, pageSize, entityClass, searchIndex);
        return Pageable.build(pageResult.getPageNumber(), pageResult.getPageSize(), pageResult.getTotal(), pageResult.getList());
    }

    /**
     * 清除scrollId
     *
     * @param scrollId scrollId值
     * @return
     */
    @Override
    public ClearScrollResponse clearScroll(String... scrollId) {
        Assert.notNull(scrollId, "scrollId can't be null.");
        return this.getESTemplate().clearScroll(scrollId);
    }

    /**
     * 清除scrollId
     *
     * @param scrollIds scrollId值列表
     * @return
     */
    @Override
    public ClearScrollResponse clearScroll(List<String> scrollIds) {
        Assert.notNull(scrollIds, "scrollIds can't be null.");
        return this.getESTemplate().clearScroll(scrollIds);
    }

    @Override
    public long selectCount() {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.fetchSource(false);
        SearchResponse response = this.query(builder);
        return response.getHits().getTotalHits().value;
    }

    @Override
    public boolean existsById(ID id) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.idsQuery().addIds(this.stringIdRepresentation(id)));
        builder.fetchSource(false);
        SearchResponse response = this.query(builder);
        long totalHits = response.getHits().getTotalHits().value;
        return totalHits == 1L;
    }

    public AbstractElasticSearchRepository() {
        this.whichDataSource = DEFAULT_DATASOURCE;
    }

    protected int getWhichDataSource() {
        return whichDataSource;
    }

    protected ESTemplate getESTemplate() {
        return this.getESTemplate(getWhichDataSource());
    }

    protected ESTemplate getESTemplate(int whichDataSource) {
        eSTemplate.setDefaultWhichDataSource(whichDataSource);
        return this.eSTemplate;
    }

    @Override
    public Class<T> getEntityClass() {
        if (Objects.isNull(entityClass)) {
            try {
                this.entityClass = resolveReturnedClassFromGenericType();
            } catch (Exception e) {
                throw new EsException("Unable to resolve EntityClass. Please use according setter!", e);
            }
        }
        return entityClass;
    }

    private MetaData getMetaData(Class<?> clazz) {
        return IndexToolkit.getMetaData(clazz);
    }

    @Override
    public MetaData getMetaData() {
        if (Objects.isNull(metaData)) {
            try {
                this.metaData = this.getMetaData(getEntityClass());
            } catch (Exception e) {
                throw new EsException("Unable to resolve MetaData. Please use according setter!", e);
            }
        }
        return metaData;
    }

    @Override
    public String[] getSearchIndexNames() {
        return getMetaData().getSearchIndexNames();
    }

    @Override
    public String getIndexName() {
        return getMetaData().getIndexName();
    }

    private Class<T> resolveReturnedClassFromGenericType() {
        Class<? extends AbstractElasticSearchRepository> current = this.getClass();
        while (!AbstractElasticSearchRepository.class.equals(current.getSuperclass())) {
            current = (Class<? extends AbstractElasticSearchRepository>) current.getSuperclass();
        }
        Type genType = current.getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class) params[0];
    }

    public final void setEntityClass(Class<T> entityClass) {
        Assert.notNull(entityClass, "EntityClass must not be null.");
        this.entityClass = entityClass;
    }

    private List<String> stringIdsRepresentation(Iterable<ID> ids) {
        Assert.notNull(ids, "ids can't be null.");
        List<String> stringIds = new ArrayList<>();
        for (ID id : ids) {
            stringIds.add(stringIdRepresentation(id));
        }
        return stringIds;
    }

    protected abstract String stringIdRepresentation(ID id);
}
