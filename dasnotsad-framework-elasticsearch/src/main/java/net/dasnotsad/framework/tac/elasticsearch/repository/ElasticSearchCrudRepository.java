package net.dasnotsad.framework.tac.elasticsearch.repository;

import net.dasnotsad.framework.tac.data.metadata.Pageable;
import net.dasnotsad.framework.tac.data.repository.PagingAndSortingRepository;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: ElasticSearch crud 操作
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public interface ElasticSearchCrudRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

    /*---------------------------------------------[ Index ]---------------------------------------------*/

    /**
     * 判断索引是否存在
     *
     * @return boolean
     */
    boolean indexExists();

    /**
     * 判断索引是否存在
     *
     * @return boolean
     */
    boolean indexExists(final String index);

    /**
     * 创建索引
     *
     * @return CreateIndexResponse
     */
    CreateIndexResponse createIndex();

    /**
     * 删除索引
     *
     * @return
     */
    AcknowledgedResponse deleteIndex();

    /**
     * 删除索引
     *
     * @param indexs 索引名数组
     * @return
     */
    AcknowledgedResponse deleteIndex(final String... indexs);

    /*---------------------------------------------[ Document ]---------------------------------------------*/

    /**
     * 删除指定属性
     *
     * @param bean 数据对象
     * @param id   主键ID
     * @param keys 删除的属性集合
     */
    void deleteAttrs(final T bean, ID id, List<String> keys);

    /**
     * 删除指定属性
     *
     * @param bean 数据对象
     * @param ids  主键ID集合
     * @param keys 删除的属性集合
     */
    void deleteAttrs(final T bean, List<ID> ids, List<String> keys);

    /**
     * 批量get查询
     *
     * @param request 查询请求
     * @return
     */
    MultiGetResponse getBatch(final MultiGetRequest request);

    /**
     * 批量search
     *
     * @param request 查询请求
     * @return
     */
    MultiSearchResponse searchBatch(final MultiSearchRequest request);

    /**
     * 批量查询
     *
     * @param sourceBuilder 查询语句对象
     * @return
     */
    Iterable<T> selectBatch(final SearchSourceBuilder sourceBuilder);

    /**
     * 根据ids查询
     *
     * @param ids     id列表
     * @param sorts   排序
     * @return
     */
    Iterable<T> selectBatchByIds(final Iterable<ID> ids, final List<SortBuilder<?>> sorts);


    /**
     * 深度分页查询
     *
     * @param currentPage         页码
     * @param pageSize            每页条数
     * @param searchSourceBuilder 查询语句对象
     * @return
     */
    Pageable<T> selectPage(final int currentPage, final int pageSize, final SearchSourceBuilder searchSourceBuilder);

    /**
     * scroll查询
     *
     * @param sourceBuilder 查询语句对象
     * @param keepAlive     scroll快照有效时间（1m代表1分钟）
     * @return
     */
    SearchResponse selectScroll(final SearchSourceBuilder sourceBuilder, final String keepAlive);

    /**
     * scroll查询
     *
     * @param scrollId  scrollId值
     * @param keepAlive scroll快照有效时间（1m代表1分钟）
     * @return
     */
    SearchResponse selectScroll(final String scrollId, final String keepAlive);

    /**
     * 清除scrollId
     *
     * @param scrollId scrollId值
     * @return
     */
    ClearScrollResponse clearScroll(final String... scrollId);

    /**
     * 清除scrollId
     *
     * @param scrollIds scrollId值列表
     * @return
     */
    ClearScrollResponse clearScroll(final List<String> scrollIds);
}
