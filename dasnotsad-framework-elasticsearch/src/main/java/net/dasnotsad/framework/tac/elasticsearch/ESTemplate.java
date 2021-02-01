package net.dasnotsad.framework.tac.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.dasnotsad.framework.tac.elasticsearch.annotation.EsDocument;
import net.dasnotsad.framework.tac.elasticsearch.annotation.EsIdentify;
import net.dasnotsad.framework.tac.elasticsearch.annotation.RestClientContainer;
import net.dasnotsad.framework.tac.elasticsearch.core.executor.RetryExecutor;
import net.dasnotsad.framework.tac.elasticsearch.core.operator.IOperator;
import net.dasnotsad.framework.tac.elasticsearch.core.operator.impl.*;
import net.dasnotsad.framework.tac.elasticsearch.core.page.*;
import net.dasnotsad.framework.tac.elasticsearch.retreat.feign.func.DelayMessageBuilder;
import net.dasnotsad.framework.tac.elasticsearch.utils.DeclaredFieldsUtil;
import net.dasnotsad.framework.tac.elasticsearch.annotation.EsVersion;
import net.dasnotsad.framework.tac.elasticsearch.core.executor.IExector;
import net.dasnotsad.framework.tac.elasticsearch.utils.MappingToolkit;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.index.query.QueryBuilders.matchPhraseQuery;

/**
 * ES7.9.3模板工具类
 *
 * @author liuliwei
 * @since 3.0.0
 */
@Component
@Slf4j
public class ESTemplate {

    @Autowired
    private RestClientContainer containers;

    @Autowired
    private DelayMessageBuilder delayMessageBuilder;

    @Autowired
    private RequestOptions requestOptions;

    @Value("${spring.application.name}")
    private String sysCode;

    public static final int DEFAULT_DATASOURCE = 0;
    private final int DEFAULT_LIMIT = 1000;// List max item
    private final int DEFAULT_MAX_LIMIT = 5000;// Delay bulk max item
    private ThreadLocal<Integer> whichDataSource;

    private IExector executor;
    private IOperator<CreateIndexRequest, CreateIndexResponse> createIndexOperator;
    private IOperator<DeleteIndexRequest, AcknowledgedResponse> deleteIndexOperator;
    private IOperator<IndexRequest, IndexResponse> insertDocumentOperator;
    private IOperator<BulkRequest, BulkResponse> bulkDocumentOperator;
    private IOperator<GetRequest, GetResponse> getDocumentOperator;
    private IOperator<MultiGetRequest, MultiGetResponse> multiGetOperator;
    private IOperator<DeleteRequest, DeleteResponse> deleteDocumentOperator;
    private IOperator<UpdateRequest, UpdateResponse> updateDocumentOperator;
    private IOperator<SearchRequest, SearchResponse> searchDocumentOperator;
    private IOperator<MultiSearchRequest, MultiSearchResponse> multiSearchOperator;
    private IOperator<SearchScrollRequest, SearchResponse> searchScrollDocumentOperator;
    private IOperator<ClearScrollRequest, ClearScrollResponse> clearScrollOperator;
    private IOperator<GetIndexRequest, Boolean> indicesExistsOperator;
    private IOperator<GetAliasesRequest, GetAliasesResponse> aliasesOperator;
    private IOperator<UpdateByQueryRequest, BulkByScrollResponse> queryUpdateOperator;
    private IOperator<DeleteByQueryRequest, BulkByScrollResponse> queryDeleteOperator;

    //异步写入存储
    private Map<Integer, Queue<IndexRequest>> delayMap;
    //当前应用创建过的或已经验证过的索引缓存，用于提升indicesExists注解验证效率
    private Map<String, String> indexExists;
    //异步写入线程
    private ScheduledExecutorService scheduledExecutorService;

    public ESTemplate(@Value("${spring.application.name}") String sysCode,
                      @Value("${dasnotsad.paas.es.delaytime:5}") Long delayTime,
                      @Autowired RestClientContainer containers,
                      @Autowired DelayMessageBuilder delayMessageBuilder) {
        executor = new RetryExecutor(sysCode, containers, delayTime, delayMessageBuilder);
        createIndexOperator = new CreateIndexOperator();
        deleteIndexOperator = new DeleteIndexOperator();
        insertDocumentOperator = new InsertDocumentOperator();
        bulkDocumentOperator = new BulkDocumentOperator();
        getDocumentOperator = new GetDocumentOperator();
        multiGetOperator = new MultiGetOperator();
        deleteDocumentOperator = new DeleteDocumentOperator();
        updateDocumentOperator = new UpdateDocumentOperator();
        searchDocumentOperator = new SearchDocumentOperator();
        multiSearchOperator = new MultiSearchOperator();
        searchScrollDocumentOperator = new SearchScrollDocumentOperator();
        clearScrollOperator = new ClearScrollOperator();
        indicesExistsOperator = new IndicesExistsOperator();
        aliasesOperator = new AliasesOperator();
        queryUpdateOperator = new QueryUpdateOperator();
        queryDeleteOperator = new QueryDeleteOperator();
        delayMap = new HashMap<>();
        whichDataSource = ThreadLocal.withInitial(() -> DEFAULT_DATASOURCE);
        indexExists = new WeakHashMap<>();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @PostConstruct
    private void initTemplate() {
        scheduledExecutorService.scheduleWithFixedDelay(new AsyncInsert(),
                3, 3, TimeUnit.SECONDS);
    }

    class AsyncInsert implements Runnable {
        @Override
        public void run() {
            try {//确保定时任务不被终止
                asyncInsert();
            } catch (Throwable e) {
                log.error("************批处理化定时任务发送异常", e.getMessage(), e);
            }
        }
    }

    /**
     * 异步批量写入实现
     */
    private void asyncInsert() {
        for (Map.Entry<Integer, Queue<IndexRequest>> entry : delayMap.entrySet()) {
            asyncInsert(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 异步批量写入实现
     */
    private void asyncInsert(int whichDataSource, Queue<IndexRequest> requests) {
        if(!requests.isEmpty()) {
            log.info("************批处理化定时任务处理中...");
            BulkRequest bulkRequest = new BulkRequest();
            loop:for (int i = 0; i < DEFAULT_MAX_LIMIT; i++) {//最多取5000条
                IndexRequest request = requests.poll();
                if (request == null)
                    break loop;
                bulkRequest.add(request);
            }
            if (!bulkRequest.requests().isEmpty()) {
                try {
                    BulkResponse response = bulk(whichDataSource, bulkRequest);
                    if (!response.hasFailures())
                        log.info("************成功处理{}条", bulkRequest.requests().size());
                    else
                        log.error("************批处理化定时任务存在失败，失败原因：{}", response.buildFailureMessage());
                } catch (Throwable e) {
                    log.error("************执行异步写入定时任务时发生异常", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 设置默认数据源
     *
     * @param whichDataSource 数据源标识
     */
    public void setDefaultWhichDataSource(int whichDataSource) {
        this.whichDataSource.set(whichDataSource);
    }

    /**
     * 获取数据源
     *
     * @param nodes 例如：10.0.0.1:9200,10.0.0.2:9200,10.0.0.3:9200
     * @return 数据源编号
     */
    public int getWhichDataSource(String nodes) {
        return containers.getWhichDataSource(nodes);
    }

    private <R, S> S exec(int whichDataSource, IOperator<R, S> operator, R request) {
        return executor.exec(whichDataSource, operator, request, requestOptions);
    }

    /**
     * 批量异步写入
     *
     * @param index  索引名
     * @param source 存储对象
     */
    public <T> void bulkAsyncInsert(final String index, final T source) {
        Objects.requireNonNull(source, "source must not be null");
        bulkAsyncInsert(whichDataSource.get(), index, source);
    }

    /**
     * 批量异步写入
     *
     * @param index   索引名
     * @param sources 存储对象列表
     */
    public <T> void bulkAsyncInsert(final String index, final List<T> sources) {
        bulkAsyncInsert(whichDataSource.get(), index,  sources);
    }

    /**
     * 批量异步写入
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param source          存储对象列表
     */
    public <T> void bulkAsyncInsert(final int whichDataSource, final String index, final T source) {
        Objects.requireNonNull(source, "source must not be null");

        autoIndex(index, source.getClass());

        Queue<IndexRequest> requests = getQueueFromMap(whichDataSource);
        IndexRequest request = new IndexRequest(index).id(getIdByIdentify(source))
                .source(JSON.toJSONString(source), XContentType.JSON);
        Long _version = getVerByVersion(source);
        request.versionType(VersionType.EXTERNAL);
        request.version(_version == null ? System.currentTimeMillis() : _version);
        requests.offer(request);
        if(requests.size() >= DEFAULT_MAX_LIMIT) {
            asyncInsert(whichDataSource, requests);
        }
    }

    /**
     * 批量异步写入
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param sources         存储对象列表
     */
    public <T> void bulkAsyncInsert(final int whichDataSource, final String index,
                                    final List<T> sources) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(sources, "sources must not be null");

        if (sources.size() >= DEFAULT_LIMIT && sources.size() <= DEFAULT_MAX_LIMIT) {

            autoIndex(index, sources.get(0).getClass());

            BulkRequest bulkRequest = new BulkRequest();
            for (T t : sources) {
                IndexRequest request = new IndexRequest(index).id(getIdByIdentify(t))
                        .source(JSON.toJSONString(t), XContentType.JSON);
                Long _version = getVerByVersion(t);
                request.versionType(VersionType.EXTERNAL);
                request.version(_version == null ? System.currentTimeMillis() : _version);
                bulkRequest.add(request);
            }
            BulkResponse response = bulk(whichDataSource, bulkRequest);
            if (response.hasFailures())
                log.warn("************批量insert失败{}条", bulkRequest.requests().size());
        } else
            for (T t : sources)
                bulkAsyncInsert(whichDataSource, index, t);
    }

    //安全添加缓存队列
    private Queue<IndexRequest> getQueueFromMap(int whichDataSource) {
        Queue<IndexRequest> requests;
        if (!delayMap.containsKey(whichDataSource))
            synchronized (this) {
                if (!delayMap.containsKey(whichDataSource)) {
                    requests = new ConcurrentLinkedQueue<>();
                    delayMap.put(whichDataSource, requests);
                } else
                    requests = delayMap.get(whichDataSource);
            }
        else
            requests = delayMap.get(whichDataSource);
        return requests;
    }

    //批量操作
    private BulkResponse bulk(int whichDataSource, BulkRequest request) {
        return exec(whichDataSource, bulkDocumentOperator, request);
    }

    /**
     * 获取全部索引别名
     *
     * @return Set
     */
    public Set<String> getAllIndices() {
        return getAliases();
    }

    /**
     * 获取全部索引别名
     *
     * @return Set
     */
    public Set<String> getAliases() {
        return getAliases(whichDataSource.get());
    }

    /**
     * 获取全部索引名
     *
     * @param whichDataSource 数据源标识
     * @return Set
     */
    public Set<String> getAliases(int whichDataSource) {
        return exec(whichDataSource, aliasesOperator, new GetAliasesRequest()).getAliases().keySet();
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引名
     * @return boolean
     */
    public boolean indexExists(final String index) {
        return indexExists(whichDataSource.get(), index);
    }

    /**
     * 判断索引是否存在
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @return boolean
     */
    public boolean indexExists(final int whichDataSource, final String index) {
        Objects.requireNonNull(index, "index must not be null");
        if (indexExists.containsKey(index))
            return true;
        else {
            GetIndexRequest request = new GetIndexRequest(index);
            boolean isExists = exec(whichDataSource, indicesExistsOperator, request);
            if (isExists)
                indexExists.put(index, index);
            return isExists;
        }
    }

    /**
     * 创建索引
     *
     * @param index    索引名
     * @param shards   分片数
     * @param replicas 副本数
     * @return CreateIndexResponse
     */
    public CreateIndexResponse createIndex(final String index, final int shards, final int replicas) {
        return createIndex(whichDataSource.get(), index, shards, replicas);
    }

    /**
     * 创建索引
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param shards          分片数
     * @param replicas        副本数
     * @return CreateIndexResponse
     */
    public CreateIndexResponse createIndex(final int whichDataSource, final String index, final int shards,
                                           final int replicas) {
        Objects.requireNonNull(index, "index must not be null");

        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(
                Settings.builder().put("index.number_of_shards", shards).put("index.number_of_replicas", replicas));
        return exec(whichDataSource, createIndexOperator, request);
    }

    /**
     * 创建索引
     *
     * @param index    索引名
     * @param shards   分片数
     * @param replicas 副本数
     * @param mapping  mapping设置
     * @return
     */
    public CreateIndexResponse createIndex(final String index, final int shards, final int replicas,
                                           final Map mapping) {
        return createIndex(whichDataSource.get(), index, shards, replicas, mapping);
    }

    /**
     * 创建索引
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param shards          分片数
     * @param replicas        副本数
     * @param mapping         mapping设置
     * @return
     */
    public CreateIndexResponse createIndex(final int whichDataSource, final String index, final int shards,
                                           final int replicas, final Map mapping) {
        Objects.requireNonNull(index, "index must not be null");

        CreateIndexRequest request = new CreateIndexRequest(index);
        request.mapping(mapping).settings(
                Settings.builder().put("index.number_of_shards", shards).put("index.number_of_replicas", replicas));
        return exec(whichDataSource, createIndexOperator, request);
    }

    /**
     * 创建索引
     *
     * @param index    索引名
     * @param shards   分片数
     * @param replicas 副本数
     * @param mapping  mapping设置
     * @return
     */
    public CreateIndexResponse createIndex(final String index, final int shards,
                                           final int replicas, final XContentBuilder mapping) {
        return createIndex(whichDataSource.get(), index, shards, replicas, mapping);
    }

    /**
     * 创建索引
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param shards          分片数
     * @param replicas        副本数
     * @param mapping         mapping设置
     * @return
     */
    public CreateIndexResponse createIndex(final int whichDataSource, final String index, final int shards,
                                           final int replicas, final XContentBuilder mapping) {
        Objects.requireNonNull(index, "index must not be null");
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.mapping(mapping).settings(
                Settings.builder().put("index.number_of_shards", shards).put("index.number_of_replicas", replicas));
        return exec(whichDataSource, createIndexOperator, request);
    }

    /**
     * 删除索引
     * *******删除的数据无法复原，谨慎使用！！！
     *
     * @param indices 索引名数组
     * @return
     */
    public AcknowledgedResponse deleteIndex(final String... indices) {
        return deleteIndex(whichDataSource.get(), indices);
    }

    /**
     * 删除索引
     * *******删除的数据无法复原，谨慎使用！！！
     *
     * @param whichDataSource 数据源标识
     * @param indices         索引名数组
     * @return
     */
    public AcknowledgedResponse deleteIndex(final int whichDataSource, final String... indices) {
        Objects.requireNonNull(indices, "index must not be null");

        for(String index : indices){
            if(indexExists(whichDataSource, index))
                indexExists.remove(index);
        }

        DeleteIndexRequest request = new DeleteIndexRequest(indices);
        return exec(whichDataSource, deleteIndexOperator, request);
    }

    /**
     * 插入文档
     *
     * @param index           索引名
     * @param source          数据对象（代）
     * @return IndexResponse
     */
    public <T> IndexResponse insertDocment(final String index, final T source) {
        String _id = getIdByIdentify(source);
        return insertDocment(index, _id, source);
    }

    /**
     * 插入文档
     *
     * @param index  索引名
     * @param id     id值
     * @param source 数据对象
     * @return
     */
    public <T> IndexResponse insertDocment(final String index, final String id, final T source) {
        return insertDocment(whichDataSource.get(), index, id, source);
    }

    /**
     * 插入文档
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param id              id值
     * @param source          数据对象
     * @return
     */
    public <T> IndexResponse insertDocment(final int whichDataSource, final String index,
                                           final String id, final T source) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(source, "source must not be null");

        autoIndex(index, source.getClass());

        IndexRequest request = new IndexRequest(index).id(id);
        Long _version = getVerByVersion(source);
        request.versionType(VersionType.EXTERNAL);
        request.version(_version == null ? System.currentTimeMillis() : _version);
        request.source(JSONObject.toJSONString(source), XContentType.JSON);
        return exec(whichDataSource, insertDocumentOperator, request);
    }

    /**
     * 批量插入文档
     *
     * @param index   索引名
     * @param sources 数据对象列表
     * @return
     */
    public <T> BulkResponse bulkInsert(final String index, final List<T> sources) {
        return bulkInsert(whichDataSource.get(), index, sources);
    }

    /**
     * 批量插入文档
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param sources         数据对象列表
     * @return
     */
    public <T> BulkResponse bulkInsert(final int whichDataSource, final String index,
                                       final List<T> sources) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(sources, "sources must not be null");
        List<String> idList = getIdByListIdentify(sources);
        return bulkInsert(whichDataSource, index, sources, idList);
    }

    /**
     * 批量插入文档
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param sources         数据对象列表
     * @param idList          主键id列表
     * @return
     */
    public <T> BulkResponse bulkInsert(final int whichDataSource, final String index,
                                       final List<T> sources, final List<String> idList) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(sources, "sources must not be null");
        Objects.requireNonNull(idList, "idList must not be null");

        autoIndex(index, sources.get(0).getClass());

        BulkRequest requests = new BulkRequest();
        for (int i = 0; i < sources.size(); i++) {
            T source = sources.get(i);
            IndexRequest request = idList.isEmpty()
                    ? new IndexRequest(index).source(JSONObject.toJSONString(source), XContentType.JSON)
                    : new IndexRequest(index).id(idList.get(i)).source(JSONObject.toJSONString(source),
                    XContentType.JSON);
            Long _version = getVerByVersion(source);
            request.versionType(VersionType.EXTERNAL);
            request.version(_version == null ? System.currentTimeMillis() : _version);
            requests.add(request);
        }
        return exec(whichDataSource, bulkDocumentOperator, requests);
    }

    /**
     * 根据id查询
     *
     * @param index 索引名，Get方法不支持模糊
     * @param _id   id值
     * @return
     */
    public GetResponse getSingleById(final String index, final String _id) {
        return getSingleById(whichDataSource.get(), index, _id);
    }

    /**
     * 根据id查询
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名，Get方法不支持模糊
     * @param _id             id值
     * @return
     */
    public GetResponse getSingleById(final int whichDataSource, final String index,
                                     final String _id) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(_id, "_id must not be null");
        return exec(whichDataSource, getDocumentOperator, new GetRequest(index, _id));
    }

    /**
     * 根据id查询
     *
     * @param index 索引名
     * @param _id   id值
     * @param clazz 反序列化对象
     * @return
     */
    public <T> T getSingleById(final String index, final String _id, final Class<T> clazz) {
        return getSingleById(whichDataSource.get(), index, _id, clazz);
    }

    /**
     * 根据id查询
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param _id             id值
     * @param clazz           反序列化对象
     * @return
     */
    public <T> T getSingleById(final int whichDataSource, final String index, final String _id,
                               final Class<T> clazz) {

        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(_id, "_id must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder bq = QueryBuilders.boolQuery();
        bq.must(matchPhraseQuery("_id", _id));
        searchSourceBuilder.query(bq);
        SearchResponse res = query(whichDataSource, index, searchSourceBuilder);
        T ret = null;
        if(res.getHits().getTotalHits().value > 0){
            ret = JSONObject.parseObject(res.getHits().getHits()[0].getSourceAsString(), clazz);
            setVersion(ret, res.getHits().getHits()[0].getVersion());//设置当前version
        }
        return ret;
    }

    /**
     * 批量get查询
     *
     * @param request 查询请求
     * @return
     */
    public MultiGetResponse multiGet(final MultiGetRequest request) {
        return multiGet(whichDataSource.get(), request);
    }

    /**
     * 批量get查询
     *
     * @param whichDataSource 数据源标识
     * @param request         查询请求
     * @return
     */
    public MultiGetResponse multiGet(final int whichDataSource, final MultiGetRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return exec(whichDataSource, multiGetOperator, request);
    }

    /**
     * 批量search
     *
     * @param request 查询请求
     * @return
     */
    public MultiSearchResponse multiSearch(final MultiSearchRequest request) {
        return multiSearch(whichDataSource.get(), request);
    }

    /**
     * 批量search
     *
     * @param whichDataSource 数据源标识
     * @param request         查询请求
     * @return
     */
    public MultiSearchResponse multiSearch(final int whichDataSource, final MultiSearchRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return exec(whichDataSource, multiSearchOperator, request);
    }

    /**
     * 根据id删除
     *
     * @param index 索引
     * @param _id   id值
     * @return
     */
    public DeleteResponse deleteById(final String index, final String _id) {
        return deleteById(whichDataSource.get(), index, _id);
    }

    /**
     * 根据id删除
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param _id             id值
     * @return
     */
    public DeleteResponse deleteById(final int whichDataSource, final String index,
                                     final String _id) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(_id, "_id must not be null");

        return exec(whichDataSource, deleteDocumentOperator, new DeleteRequest(index, _id));
    }

    /**
     * 批量删除
     *
     * @param index   索引名
     * @param _idList id列表
     * @return
     */
    public BulkResponse bulkDelete(final String index, final List<String> _idList) {
        return bulkDelete(whichDataSource.get(), index, _idList);
    }

    /**
     * 批量删除
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param _idList         id列表
     * @return
     */
    public BulkResponse bulkDelete(final int whichDataSource, final String index,
                                   final List<String> _idList) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(_idList, "_idList must not be null");

        BulkRequest request = new BulkRequest();
        _idList.forEach(_id -> request.add(new DeleteRequest(index, _id)));
        return exec(whichDataSource, bulkDocumentOperator, request);
    }

    /**
     * 根据条件删除
     *
     * @param request         条件删除对象
     * @return BulkByScrollResponse 删除返回
     */
    public BulkByScrollResponse queryDelete(final DeleteByQueryRequest request){
        return queryDelete(whichDataSource.get(), request);
    }

    /**
     * 根据条件删除
     *
     * @param whichDataSource 数据源标识
     * @param request         条件删除对象
     * @return BulkByScrollResponse 删除返回
     */
    public BulkByScrollResponse queryDelete(final int whichDataSource, final DeleteByQueryRequest request){
        return exec(whichDataSource, queryDeleteOperator, request);
    }

    /**
     * 根据id更新
     *
     * @param index  索引名
     * @param _id    id值
     * @param source 数据对象
     * @return
     */
    public <T> UpdateResponse updateById(final String index, final String _id, final T source) {
        return updateById(whichDataSource.get(), index, _id, source);
    }

    /**
     * 根据id更新
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param _id             id值
     * @param source          数据对象
     * @return
     */
    public <T> UpdateResponse updateById(final int whichDataSource, final String index,
                                         final String _id, final T source) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(_id, "_id must not be null");

        UpdateRequest request = new UpdateRequest(index, _id);
        request.doc(JSONObject.toJSONString(source), XContentType.JSON);
        Long _version = getVerByVersion(source);
        if (_version != null)
            request.version(_version);
        return exec(whichDataSource, updateDocumentOperator, request);
    }

    /**
     * 批量修改
     *
     * @param index   索引名
     * @param sources 数据对象列表
     * @return
     */
    public <T> BulkResponse bulkUpdate(final String index, final List<T> sources) {
        return bulkUpdate(whichDataSource.get(), index, sources);
    }

    /**
     * 批量修改
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param sources         数据对象列表
     * @return
     */
    public <T> BulkResponse bulkUpdate(final int whichDataSource, final String index,
                                       final List<T> sources) {
        Objects.requireNonNull(index, "index must not be null");
        Objects.requireNonNull(sources, "sources must not be null");

        BulkRequest requests = new BulkRequest();
        List<String> idList = getIdByListIdentify(sources);
        if (idList.size() > 0) {
            for (int i = 0; i < sources.size(); i++) {
                T source = sources.get(i);
                UpdateRequest request = new UpdateRequest(index, idList.get(i)).doc(JSONObject.toJSONString(source),
                        XContentType.JSON);
                Long _version = getVerByVersion(source);
                if (_version != null)
                    request.version(_version);
                requests.add(request);
            }
        }
        return exec(whichDataSource, bulkDocumentOperator, requests);
    }

    /**
     * 根据条件修改
     *
     * @param request         条件修改对象
     * @return BulkByScrollResponse 修改返回
     */
    public BulkByScrollResponse queryUpdate(final UpdateByQueryRequest request){
        return queryUpdate(whichDataSource.get(), request);
    }

    /**
     * 根据条件修改
     *
     * @param whichDataSource 数据源标识
     * @param request         条件修改对象
     * @return BulkByScrollResponse 修改返回
     */
    public BulkByScrollResponse queryUpdate(final int whichDataSource, final UpdateByQueryRequest request){
        return exec(whichDataSource, queryUpdateOperator, request);
    }

    /**
     * 查询
     *
     * @param request   基本搜索条件
     * @return
     */
    public SearchResponse query(final SearchRequest request) {
        return exec(whichDataSource.get(), searchDocumentOperator, request);
    }

    /**
     * 查询
     *
     * @param index         索引名
     * @param sourceBuilder 查询语句对象
     * @return
     */
    public SearchResponse query(final String index, final SearchSourceBuilder sourceBuilder) {
        return query(whichDataSource.get(), sourceBuilder, index);
    }

    /**
     * 查询
     *
     * @param whichDataSource 数据源标识
     * @param index           索引名
     * @param sourceBuilder   查询语句对象
     * @return
     */
    public SearchResponse query(final int whichDataSource, final String index,
                                final SearchSourceBuilder sourceBuilder) {
        return query(whichDataSource, sourceBuilder, index);
    }

    /**
     * 查询
     *
     * @param whichDataSource 数据源标识
     * @param sourceBuilder   查询语句对象
     * @param indices         索引名数组
     * @return
     */
    public SearchResponse query(final int whichDataSource, final SearchSourceBuilder sourceBuilder,
                                final String... indices) {
        Objects.requireNonNull(sourceBuilder, "sourceBuilder must not be null");
        Objects.requireNonNull(indices, "indices must not be null");

        SearchRequest request = new SearchRequest(indices);
        request.source(sourceBuilder);
        return exec(whichDataSource, searchDocumentOperator, request);
    }

    /**
     * 根据ids查询
     *
     * @param idList id列表
     * @param index  索引名
     * @param clazz  反序列化对象
     * @return
     */
    public <T> List<T> queryByIds(final List<String> idList, final String index,
                                  final Class<T> clazz) {
        return queryByIds(idList, clazz, index);
    }

    /**
     * 根据ids查询
     *
     * @param idList  id列表
     * @param clazz   反序列化对象
     * @param indices 索引名数组
     * @return
     */
    public <T> List<T> queryByIds(final List<String> idList, final Class<T> clazz,
                                  final String... indices) {
        return queryByIds(idList, clazz, null, indices);
    }

    /**
     * 根据ids查询
     *
     * @param idList   id列表
     * @param clazz    反序列化对象
     * @param sortList 排序
     * @param indices  索引数组
     * @return
     */
    public <T> List<T> queryByIds(final List<String> idList, final Class<T> clazz,
                                  final List<SortBuilder<?>> sortList, final String... indices) {
        return queryByIds(whichDataSource.get(), idList, clazz, sortList, indices);
    }

    /**
     * 根据ids查询
     *
     * @param whichDataSource 数据源标识
     * @param idList          id列表
     * @param clazz           反序列化对象
     * @param sortList        排序
     * @param indices         索引数组
     * @return
     */
    public <T> List<T> queryByIds(final int whichDataSource, final List<String> idList, final Class<T> clazz,
                                  final List<SortBuilder<?>> sortList, final String... indices) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.idsQuery().addIds(idList.toArray(new String[idList.size()])));
        sourceBuilder.size(idList.size());
        if (sortList != null && !sortList.isEmpty())
            sortList.forEach(sort -> sourceBuilder.sort(sort));
        SearchResponse response = query(whichDataSource, sourceBuilder, indices);
        SearchHits hits = response.getHits();
        List<T> retList = new ArrayList<>();
        if (hits.getTotalHits().value == 0)
            return retList;
        SearchHit[] searchHits = hits.getHits();
        if (clazz != SearchHit.class)
            for (SearchHit sc : searchHits) {
                T ret = JSON.parseObject(sc.getSourceAsString(), clazz);
                setVersion(ret, sc.getVersion());
                retList.add(ret);
            }
        else
            for (SearchHit sc : searchHits)
                retList.add((T) sc);
        return retList;
    }

    /**
     * scroll查询
     *
     * @param index         索引名
     * @param sourceBuilder 查询语句对象
     * @param keepAlive     scroll快照有效时间（1m代表1分钟）
     * @return
     */
    public SearchResponse queryScroll(final String index, final SearchSourceBuilder sourceBuilder,
                                      final String keepAlive) {
        return queryScroll(sourceBuilder, keepAlive, index);
    }

    /**
     * scroll查询
     *
     * @param sourceBuilder 查询语句对象
     * @param keepAlive     scroll快照有效时间（1m代表1分钟）
     * @param indices       索引名数组
     * @return
     */
    public SearchResponse queryScroll(final SearchSourceBuilder sourceBuilder,
                                      final String keepAlive, final String... indices) {
        return queryScroll(whichDataSource.get(), sourceBuilder, keepAlive, indices);
    }

    /**
     * scroll查询
     *
     * @param whichDataSource 数据源标识
     * @param sourceBuilder   查询语句对象
     * @param keepAlive       scroll快照有效时间（1m代表1分钟）
     * @param indices         索引名数组
     * @return
     */
    public SearchResponse queryScroll(final int whichDataSource,
                                      final SearchSourceBuilder sourceBuilder, final String keepAlive, final String... indices) {

        Objects.requireNonNull(sourceBuilder, "sourceBuilder must not be null");
        Objects.requireNonNull(indices, "indices must not be null");

        SearchRequest request = new SearchRequest(indices);
        request.source(sourceBuilder);
        request.scroll(keepAlive);
        SearchResponse response = exec(whichDataSource, searchDocumentOperator, request);
        return response;
    }

    /**
     * scroll查询
     *
     * @param scrollId  scrollId值
     * @param keepAlive scroll快照有效时间（1m代表1分钟）
     * @return
     */
    public SearchResponse queryScroll(final String scrollId, final String keepAlive) {
        return queryScroll(whichDataSource.get(), scrollId, keepAlive);
    }

    /**
     * scroll查询
     *
     * @param whichDataSource 数据源标识
     * @param scrollId        scrollId值
     * @param keepAlive       scroll快照有效时间（1m代表1分钟）
     * @return
     */
    public SearchResponse queryScroll(final int whichDataSource, final String scrollId, final String keepAlive) {
        Objects.requireNonNull(scrollId, "scrollId must not be null");

        SearchScrollRequest request = new SearchScrollRequest(scrollId);
        request.scroll(keepAlive == null ? "1m" : keepAlive);
        SearchResponse response = exec(whichDataSource, searchScrollDocumentOperator, request);
        return response;
    }

    /**
     * 深度分页查询
     *
     * @param searchSourceBuilder 查询语句对象
     * @param pageNumber          页码
     * @param pageSize            每页条数
     * @param clazz               反序列化对象
     * @param indices             索引名数组
     * @return
     */
    public <T> PageResult<T> searchScrollDeepPaging(final SearchSourceBuilder searchSourceBuilder,
                                                    final int pageNumber, final int pageSize, final Class<T> clazz,
                                                    final String... indices) {
        return searchScrollDeepPaging(whichDataSource.get(), searchSourceBuilder, pageNumber, pageSize, clazz, indices);
    }

    /**
     * 深度分页查询
     *
     * @param whichDataSource     数据源标识
     * @param searchSourceBuilder 查询语句对象
     * @param pageNumber          页码
     * @param pageSize            每页条数
     * @param clazz               反序列化对象
     * @param indices             索引名数组
     * @return
     */
    public <T> PageResult<T> searchScrollDeepPaging(final int whichDataSource,
                                                    final SearchSourceBuilder searchSourceBuilder, final int pageNumber, final int pageSize,
                                                    final Class<T> clazz, final String... indices) {
        return searchScrollDeepPaging(whichDataSource, searchSourceBuilder, pageNumber, pageSize, indices, clazz);
    }

    /**
     * 深度分页查询
     *
     * @param whichDataSource     数据源标识
     * @param searchSourceBuilder 查询语句对象
     * @param pageNumber          页码
     * @param pageSize            每页条数
     * @param indices             索引名数组
     * @param clazz               反序列化对象
     * @return
     */
    public <T> PageResult<T> searchScrollDeepPaging(final int whichDataSource,
                                                    final SearchSourceBuilder searchSourceBuilder, final int pageNumber, final int pageSize,
                                                    final String[] indices, final Class<T> clazz) {
        Objects.requireNonNull(searchSourceBuilder, "searchSourceBuilder must not be null");
        Objects.requireNonNull(indices, "indices must not be null");
        Objects.requireNonNull(clazz, "clazz must not be null");
        if (pageSize > 10000)
            throw new RuntimeException("分页pageSize最大不能超过10000");
        SearchResponse response;
        List<T> tss = new ArrayList<>();
        //打开count追踪
        searchSourceBuilder.trackTotalHits(true);
        //******2019-12-26 深度分页优化：若没有设置排序，则按照ID倒序排列
        if (searchSourceBuilder.sorts() == null || searchSourceBuilder.sorts().isEmpty())
            searchSourceBuilder.sort("_id", SortOrder.DESC);
        int max_result = pageNumber * pageSize;// 计算from+size=(pageNumber-1)*pageSize + pageSize
        if (max_result <= 10000) { // ES的index.max_result_window默认为10000，官方推荐不更改
            int from = (pageNumber - 1) * pageSize;
            searchSourceBuilder.from(from < 0 ? 0 : from).size(pageSize);
            response = getSearchResponse(whichDataSource, searchSourceBuilder, indices, clazz, tss);
        } else {// 若大于限制则做深度分页
            searchSourceBuilder.fetchSource(false);// scroll途中不抓取数据，减少IO开销
            response = query(whichDataSource, searchSourceBuilder, indices);
            //******2019-12-26 深度分页优化：判断需要达到的页码距离
            long totalHits = response.getHits().getTotalHits().value;
            boolean isReversed = max_result > totalHits / 2;//是否需要逆向搜索
            if (!isReversed) {// 顺序搜索
                List<String> idList = scrollDeepPaging(whichDataSource, searchSourceBuilder, pageNumber,
                        pageSize, indices);
                tss.addAll(queryByIds(whichDataSource, idList, clazz, searchSourceBuilder.sorts(), indices));
            } else {// 逆向搜索
                reverseSorts(searchSourceBuilder);// 排序反转
                int totalPage = Long.valueOf(totalHits % pageSize == 0 ? totalHits / pageSize : totalHits / pageSize + 1).intValue();
                int rePageNumber = totalPage - pageNumber + 1;// 计算反转后的页码
                int reMax_result = rePageNumber * pageSize;
                int lastPageSize = Long.valueOf(totalHits % pageSize == 0 ? pageSize : totalHits % pageSize).intValue();// 计算最后一页的size
                if (reMax_result <= 10000) {
                    if (rePageNumber <= 1)// 若是最后一页（反转后的第一页），则直接查询
                        searchSourceBuilder.from(0).size(lastPageSize);
                    else {// 否则跳过lastPageSize的一页数量
                        int from = (rePageNumber - 2) * pageSize + lastPageSize;
                        searchSourceBuilder.from(from < 0 ? 0 : from).size(pageSize);
                    }
                    response = getSearchResponse(whichDataSource, searchSourceBuilder, indices, clazz, tss);
                    Collections.reverse(tss);// 反转回来
                } else {
                    List<String> idList = scrollDeepPaging(whichDataSource, searchSourceBuilder, rePageNumber,
                            pageSize, indices);
                    idList = pageNumber == totalPage ? idList.subList(0, lastPageSize) : idList;
                    reverseSorts(searchSourceBuilder);// 反转回来
                    tss.addAll(queryByIds(whichDataSource, idList, clazz, searchSourceBuilder.sorts(), indices));
                }
            }
        }
        PageResult<T> result = new PageResult<>();
        result.setList(tss);
        result.setPageNumber(pageNumber);
        result.setPageSize(pageSize);
        result.setTotal(response.getHits().getTotalHits().value);
        result.setAggs(response.getAggregations());
        return result;
    }

    private <T> SearchResponse getSearchResponse(int whichDataSource, SearchSourceBuilder searchSourceBuilder, String[] indices, Class<T> clazz, List<T> tss) {
        searchSourceBuilder.fetchSource(true);// 这里直接出数据
        SearchResponse response = query(whichDataSource, searchSourceBuilder, indices);
        SearchHit[] hits = response.getHits().getHits();
        if (hits != null && hits.length > 0)
            if (clazz != SearchHit.class)
                for (SearchHit hit : hits) {
                    T ret = JSON.parseObject(hit.getSourceAsString(), clazz);
                    setVersion(ret, hit.getVersion());
                    tss.add(ret);
                }
            else
                for (SearchHit hit : hits)
                    tss.add((T) hit);
        return response;
    }

    //深度分页用循环scroll
    private List<String> scrollDeepPaging(final int whichDataSource,
                                          final SearchSourceBuilder searchSourceBuilder, final int pageNumber,
                                          final int pageSize, final String[] indices) {
        List<String> idList = new ArrayList<>();// ids存储
        int count = 10000 / pageSize;// 一次scroll页数
        int size = pageSize * count;// size取小于10000的最大倍数
        searchSourceBuilder.from(0);// 确定没有from，以免报异常
        searchSourceBuilder.size(size);
        searchSourceBuilder.fetchSource(false);// scroll途中不抓取数据，减少IO开销
        SearchResponse response = queryScroll(whichDataSource, searchSourceBuilder, "1m", indices);
        int nowPageNum = count;// 起始位置
        loop:for (;;) {
            if (nowPageNum < pageNumber && response.getHits().getHits() != null
                    && response.getHits().getHits().length > 0) {
                response = queryScroll(whichDataSource, response.getScrollId(), "1m");
                nowPageNum += count;
            } else
                break loop;
        }
        SearchHit[] hits = response.getHits().getHits();
        int mod = pageNumber % count;
        // 计算从当前scroll起的取值位置
        int start = mod == 0L ? size - pageSize : (mod - 1) * pageSize;
        for (int i = 0; i < pageSize; i++) {
            int index = start + i;
            if (index < hits.length)
                idList.add(hits[index].getId());
        }
        clearScroll(whichDataSource, response.getScrollId());
        return idList;
    }

    //将搜索条件的排序反转
    private void reverseSorts(final SearchSourceBuilder searchSourceBuilder) {
        if (searchSourceBuilder.sorts() != null)
            searchSourceBuilder.sorts().forEach(s -> s.order(s.order().equals(SortOrder.DESC) ? SortOrder.ASC : SortOrder.DESC));
    }

    /**
     * 清除scrollId
     *
     * @param scrollId scrollId值
     * @return
     */
    public ClearScrollResponse clearScroll(final String... scrollId) {
        return clearScroll(whichDataSource.get(), Arrays.asList(scrollId));
    }

    /**
     * 清除scrollId
     *
     * @param whichDataSource 数据源标识
     * @param scrollId        scrollId值
     * @return
     */
    public ClearScrollResponse clearScroll(final int whichDataSource, final String... scrollId) {
        return clearScroll(whichDataSource, Arrays.asList(scrollId));
    }

    /**
     * 清除scrollId
     *
     * @param scrollIds scrollId值列表
     * @return
     */
    public ClearScrollResponse clearScroll(final List<String> scrollIds) {
        return clearScroll(whichDataSource.get(), scrollIds);
    }

    /**
     * 清除scrollId
     *
     * @param whichDataSource 数据源标识
     * @param scrollIds       scrollId值列表
     * @return
     */
    public ClearScrollResponse clearScroll(final int whichDataSource, final List<String> scrollIds) {
        Objects.requireNonNull(scrollIds, "scrollId must not be null");
        ClearScrollRequest request = new ClearScrollRequest();
        request.scrollIds(scrollIds);
        return exec(whichDataSource, clearScrollOperator, request);
    }

    /**
     * 创建文档（EsDocument注解形式）
     *
     * @param bean 数据对象
     * @return
     */
    public <T> IndexResponse createDocument(final T bean) {
        Objects.requireNonNull(bean, "bean must not be null");
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return createDocument(bean, doc.indexName(), doc);
    }

    /**
     * 创建文档（EsDocument注解形式）
     *
     * @param bean      数据对象
     * @param indexName 索引名
     * @return
     */
    public <T> IndexResponse createDocument(final T bean, final String indexName) {
        Objects.requireNonNull(bean, "bean must not be null");
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        return createDocument(bean, indexName, doc);
    }

    private <T> IndexResponse createDocument(final T bean, final String indexName, final EsDocument doc) {
        Field field = DeclaredFieldsUtil.findAnnotation(bean.getClass(), EsIdentify.class);
        Objects.requireNonNull(field, "@EsIdentify not found");
        try {
            String id = field.get(bean).toString();
            return insertDocment(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, id, bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 异步方式创建文档（EsDocument注解形式）
     *
     * @param bean 数据对象
     * @return
     */
    public <T> void createDocumentAsync(final T bean) {
        Objects.requireNonNull(bean, "bean must not be null");
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        createDocumentAsync(bean, doc.indexName(), doc);
    }

    /**
     * 异步方式创建文档（EsDocument注解形式）
     *
     * @param bean      数据对象
     * @param indexName 索引名
     * @return
     */
    public <T> void createDocumentAsync(final T bean, final String indexName) {
        Objects.requireNonNull(bean, "bean must not be null");
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        createDocumentAsync(bean, indexName, doc);
    }

    private <T> void createDocumentAsync(final T bean, final String indexName, final EsDocument doc) {
        Field field = DeclaredFieldsUtil.findAnnotation(bean.getClass(), EsIdentify.class);
        Objects.requireNonNull(field, "@EsIdentify not found");
        bulkAsyncInsert(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, bean);
    }

    /**
     * 批量创建文档（EsDocument注解形式）
     *
     * @param beans 数据对象列表
     * @return
     */
    public <T> BulkResponse bulkCreateDocument(final List<T> beans) {
        Objects.requireNonNull(beans);
        Class<?> clazz = beans.get(0).getClass();
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return bulkCreateDocument(beans, doc.indexName(), doc);
    }

    /**
     * 批量创建文档（EsDocument注解形式）
     *
     * @param beans     数据对象列表
     * @param indexName 索引名
     * @return
     */
    public <T> BulkResponse bulkCreateDocument(final List<T> beans, final String indexName) {
        Objects.requireNonNull(beans);
        Class<?> clazz = beans.get(0).getClass();
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        return bulkCreateDocument(beans, indexName, doc);
    }

    private <T> BulkResponse bulkCreateDocument(final List<T> beans, final String indexName, final EsDocument doc) {
        return bulkInsert(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, beans);
    }

    /**
     * 异步方式批量创建文档（EsDocument注解形式）
     *
     * @param beans 数据对象列表
     * @return
     */
    public <T> void bulkCreateDocumentAsync(final List<T> beans) {
        Objects.requireNonNull(beans);
        Class<?> clazz = beans.get(0).getClass();
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        bulkCreateDocumentAsync(beans, doc.indexName(), doc);
    }

    /**
     * 异步方式批量创建文档（EsDocument注解形式）
     *
     * @param beans     数据对象列表
     * @param indexName 索引名
     * @return
     */
    public <T> void bulkCreateDocumentAsync(final List<T> beans, final String indexName) {
        Objects.requireNonNull(beans);
        Class<?> clazz = beans.get(0).getClass();
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        bulkCreateDocumentAsync(beans, indexName, doc);
    }

    private <T> void bulkCreateDocumentAsync(final List<T> beans, final String indexName, final EsDocument doc) {
        bulkAsyncInsert(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, beans);
    }

    /**
     * 查询文档（EsDocument注解形式）
     *
     * @param sourceBuilder 查询语句对象
     * @param clazz         反序列化对象
     * @return
     */
    public <T> List<T> retrieveDocument(final SearchSourceBuilder sourceBuilder, final Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return retrieveDocument(sourceBuilder, clazz, doc, doc.indexName());
    }

    /**
     * 查询文档（EsDocument注解形式）
     *
     * @param sourceBuilder 查询语句对象
     * @param clazz         反序列化对象
     * @param indices       索引名数组
     * @return
     */
    public <T> List<T> retrieveDocument(final SearchSourceBuilder sourceBuilder, final Class<T> clazz, final String... indices) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        return retrieveDocument(sourceBuilder, clazz, doc, indices);
    }

    private <T> List<T> retrieveDocument(final SearchSourceBuilder sourceBuilder, final Class<T> clazz, final EsDocument doc,
                                         final String... indices) {
        SearchResponse response = query(doc==null ? whichDataSource.get() : doc.whichDataSource(), sourceBuilder, indices);
        SearchHits hits = response.getHits();
        if (hits.getTotalHits().value == 0)
            return Collections.EMPTY_LIST;
        List<T> retList = new ArrayList<>(hits.getHits().length);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit sc : searchHits) {
            T t = JSON.parseObject(sc.getSourceAsString(), clazz);
            setVersion(t, sc.getVersion());
            retList.add(t);
        }
        return retList;
    }

    /**
     * 根据ID值查询文档（EsDocument注解形式）
     *
     * @param idList id值列表
     * @param clazz  反序列化对象
     * @return
     */
    public <T> List<T> retrieveDocumentByIds(final List<String> idList, final Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return retrieveDocumentByIds(idList, clazz, doc, doc.indexName());
    }

    /**
     * 根据ID值查询文档（EsDocument注解形式）
     *
     * @param idList  id值列表
     * @param clazz   反序列化对象
     * @param indices 索引名数组
     * @return
     */
    public <T> List<T> retrieveDocumentByIds(final List<String> idList, final Class<T> clazz, final String... indices) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        return retrieveDocumentByIds(idList, clazz, doc, indices);
    }

    private <T> List<T> retrieveDocumentByIds(final List<String> idList, final Class<T> clazz, final EsDocument doc,
                                              final String... indices) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.idsQuery().addIds(idList.toArray(new String[idList.size()])));
        sourceBuilder.size(idList.size());
        SearchResponse response = query(doc==null ? whichDataSource.get() : doc.whichDataSource(), sourceBuilder, indices);
        SearchHits hits = response.getHits();
        if (hits.getTotalHits().value == 0)
            return Collections.EMPTY_LIST;
        List<T> retList = new ArrayList<>(hits.getHits().length);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit sc : searchHits) {
            T t = JSON.parseObject(sc.getSourceAsString(), clazz);
            setVersion(t, sc.getVersion());
            retList.add(t);
        }
        return retList;
    }

    /**
     * 根据ID值查询文档（EsDocument注解形式）
     *
     * @param id    id值
     * @param clazz 反序列化对象
     * @return
     */
    public <T> T retrieveDocumentById(final String id, final Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return getSingleById(doc.whichDataSource(), doc.indexName(), id, clazz);
    }

    /**
     * 根据ID值查询文档（EsDocument注解形式）
     *
     * @param id        id值
     * @param clazz     反序列化对象
     * @param indexName 索引名
     * @return
     */
    public <T> T retrieveDocumentById(final String id, final Class<T> clazz, final String indexName) {
        Objects.requireNonNull(clazz, "clazz must not be null");
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        return getSingleById(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, id, clazz);
    }

    /**
     * 更新文档（EsDocument注解形式）
     *
     * @param bean 数据对象
     * @return
     */
    public <T> UpdateResponse updateDocument(final T bean) {
        Objects.requireNonNull(bean);
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return updateDocument(bean, doc.indexName(), doc);
    }

    /**
     * 更新文档（EsDocument注解形式）
     *
     * @param bean      数据对象
     * @param indexName 索引名
     * @return
     */
    public <T> UpdateResponse updateDocument(final T bean, final String indexName) {
        Objects.requireNonNull(bean);
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        return updateDocument(bean, indexName, doc);
    }

    private <T> UpdateResponse updateDocument(final T bean, final String indexName, final EsDocument doc) {
        Field field = DeclaredFieldsUtil.findAnnotation(bean.getClass(), EsIdentify.class);
        Objects.requireNonNull(field, "@EsIdentify not found");
        try {
            String id = field.get(bean).toString();
            return updateById(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, id, bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量更新文档（EsDocument注解形式）
     *
     * @param beans 数据对象列表
     * @return
     */
    public <T> BulkResponse bulkUpdateDocument(final List<T> beans) {
        Objects.requireNonNull(beans);
        Class<?> clazz = beans.get(0).getClass();
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return bulkUpdate(doc.whichDataSource(), doc.indexName(), beans);
    }

    /**
     * 批量更新文档（EsDocument注解形式）
     *
     * @param beans     数据对象列表
     * @param indexName 索引名
     * @return
     */
    public <T> BulkResponse bulkUpdateDocument(final List<T> beans, final String indexName) {
        Objects.requireNonNull(beans);
        Class<?> clazz = beans.get(0).getClass();
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        return bulkUpdate(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, beans);
    }

    /**
     * 根据id值删除文档（EsDocument注解形式）
     *
     * @param bean 数据对象
     * @return
     */
    public <T> DeleteResponse deleteDocument(final T bean) {
        Objects.requireNonNull(bean);
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return deleteDocument(bean, doc.indexName(), doc);
    }

    /**
     * 根据id值删除文档（EsDocument注解形式）
     *
     * @param bean      数据对象
     * @param indexName 索引名
     * @return
     */
    public <T> DeleteResponse deleteDocument(final T bean, final String indexName) {
        Objects.requireNonNull(bean);
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        return deleteDocument(bean, indexName, doc);
    }

    private <T> DeleteResponse deleteDocument(final T bean, final String indexName, final EsDocument doc) {
        Field field = DeclaredFieldsUtil.findAnnotation(bean.getClass(), EsIdentify.class);
        Objects.requireNonNull(field, "@EsIdentify not found");
        try {
            String id = field.get(bean).toString();
            return deleteById(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除文档（EsDocument注解形式）
     *
     * @param beans 数据对象列表
     * @return
     */
    public <T> BulkResponse bulkDeleteDocument(final List<T> beans) {
        Objects.requireNonNull(beans);
        Class<?> clazz = beans.get(0).getClass();
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        return bulkDeleteDocument(beans, doc.indexName(), doc);
    }

    /**
     * 批量删除文档（EsDocument注解形式）
     *
     * @param beans     数据对象列表
     * @param indexName 索引名
     * @return
     */
    public <T> BulkResponse bulkDeleteDocument(final List<T> beans, final String indexName) {
        Objects.requireNonNull(beans);
        Class<?> clazz = beans.get(0).getClass();
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        return bulkDeleteDocument(beans, indexName, doc);
    }

    private <T> BulkResponse bulkDeleteDocument(final List<T> beans, final String indexName, final EsDocument doc) {
        List<String> idList = getIdByListIdentify(beans);
        return bulkDelete(doc==null ? whichDataSource.get() : doc.whichDataSource(), indexName, idList);
    }

    //将ID值反射出来
    private <T> List<String> getIdByListIdentify(List<T> sources) {
        List<String> idList = new ArrayList<>();
        try {
            if (!sources.isEmpty()) {
                Field field = DeclaredFieldsUtil.findAnnotation(sources.get(0).getClass(), EsIdentify.class);
                if (null != field) {
                    for (T source : sources) {
                        Objects.requireNonNull(field.get(source), "@EsIdentify must be set");
                        idList.add(field.get(source).toString());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return idList;
    }

    //将ID值反射出来
    private String getIdByIdentify(Object source) {
        Field field = DeclaredFieldsUtil.findAnnotation(source.getClass(), EsIdentify.class);
        try {
            if (field != null && field.get(source) != null) {
                return field.get(source).toString();
            } else {
                throw new RuntimeException("@EsIdentify must be set");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //将version值反射出来
    private Long getVerByVersion(Object source) {
        Field field = DeclaredFieldsUtil.findAnnotation(source.getClass(), EsVersion.class);
        try {
            if (field != null && field.get(source) != null) {
                return (Long) field.get(source);
            } else {
                return null;
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //设置version值
    private void setVersion(Object source, long version) {
        Field field = DeclaredFieldsUtil.findAnnotation(source.getClass(), EsVersion.class);
        if (field != null)
            try {
                field.set(source, version);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
    }

    //说明，这个remove里面如果没有单引号的话，会报错
    private final String REMOVE_SCRIPT = "ctx._source.remove('%s')";
    private final String ID_FORMAT = "current id is [%s]";

    /**
     * 删除指定属性
     *
     * @param bean 数据对象
     * @param keys 删除的属性集合
     */
    public <T> void deleteAttrsByObj(final T bean, List<String> keys) {
        Objects.requireNonNull(bean);
        Field field = DeclaredFieldsUtil.findAnnotation(bean.getClass(), EsIdentify.class);
        Objects.requireNonNull(field, "@EsIdentify not found");
        try {
            String id = field.get(bean).toString();
            List<String> ids = new ArrayList<>(1);
            ids.add(id);
            deleteAttrsByObj(bean, ids, keys);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 删除指定属性
     *
     * @param bean 数据对象
     * @param id   主键ID
     * @param keys 删除的属性集合
     */
    public <T> void deleteAttrsByObj(final T bean, String id, List<String> keys) {
        Objects.requireNonNull(id);
        List<String> ids = new ArrayList<>(1);
        ids.add(id);
        deleteAttrsByObj(bean, ids, keys);
    }

    /**
     * 删除指定属性
     *
     * @param bean 数据对象
     * @param ids  主键ID集合
     * @param keys 删除的属性集合
     */
    public <T> void deleteAttrsByObj(final T bean, List<String> ids, List<String> keys) {
        Objects.requireNonNull(bean);
        Objects.requireNonNull(ids);
        Objects.requireNonNull(keys);
        EsDocument doc = bean.getClass().getAnnotation(EsDocument.class);
        Objects.requireNonNull(doc, "@EsDocument not found");
        deleteAttrsByParams(doc.indexName(), ids, keys, doc.whichDataSource());
    }

    /**
     * 删除指定属性
     *
     * @param indexName       索引名称
     * @param id              主键ID
     * @param keys            删除的属性集合
     * @param whichDataSource 指定数据源下标
     */
    public void deleteAttrsByParams(String indexName, String id, List<String> keys, int whichDataSource) {
        Objects.requireNonNull(id);
        List<String> ids = new ArrayList<>(1);
        ids.add(id);
        deleteAttrsByParams(indexName, ids, keys, whichDataSource);
    }

    /**
     * 删除指定属性
     *
     * @param indexName       索引名称
     * @param ids             主键ID集合
     * @param keys            删除的属性集合
     * @param whichDataSource 指定数据源下标
     */
    public void deleteAttrsByParams(String indexName, List<String> ids, List<String> keys, int whichDataSource) {
        Objects.requireNonNull(indexName);
        Objects.requireNonNull(ids);
        Objects.requireNonNull(keys);
        RestHighLevelClient restHighLevelClient = containers.get(whichDataSource);
        ids.forEach(d -> {
            UpdateRequest updateRequest = new UpdateRequest(indexName, String.valueOf(d));
            keys.forEach(key -> {
                String updateScript = String.format(REMOVE_SCRIPT, key);
                updateRequest.script(new Script(updateScript));
                try {
                    restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    log.error("when delete custom attr error, " + String.format(ID_FORMAT, d), e);
                }
            });
        });
    }

    /**
     * 自动创建索引
     *
     * @param indexName 索引名数据源标识
     * @param clazz     类
     */
    private void autoIndex(String indexName, Class<?> clazz){
        EsDocument doc = clazz.getAnnotation(EsDocument.class);
        if (doc != null && doc.createIndex() && !indexExists(doc.whichDataSource(), indexName)){
            XContentBuilder mapping = MappingToolkit.createMapping(clazz);
            createIndex(doc.whichDataSource(), indexName, doc.shards(), doc.replicas(), mapping);
        }
    }
}