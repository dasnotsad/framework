/**
 * FileName: PagenationCondtion
 * Author:   tumq
 * Date:     2018/12/19 19:57
 * Description: 页码条件内
 */
package net.dasnotsad.framework.elasticsearch.core.page;

import lombok.Data;
import net.dasnotsad.framework.elasticsearch.core.page.cache.PageNoGroup;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

import net.dasnotsad.framework.elasticsearch.core.page.cache.IPageCache;

/**
 * 〈一句话功能简述〉<br>
 * 〈页码条件内〉
 *
 * @author tumq
 * @create 2018/12/19
 */
@Data
public class PagenationCondtion {
    /**
     * 查询输入页码
     */
    private final int inputPageNo;
    /**
     * 查询输入每页大小
     */
    private final int inputPageSize;

    private ESPagenation esPagenation;

    /*查询条件*/
    private QueryBuilder inputBuilder;

    /*每次查询最大查询记录数*/
    private int maxQueryNum = 10000;

    private PagenationCondtion preCondtion;

    PagenationCondtion(int inputPageNumber, int inputPageSize) {
        this.inputPageNo = inputPageNumber;
        this.inputPageSize = inputPageSize;
    }

    public QueryBuilder getInputBuilder() {
        return inputBuilder;
    }

    public void setInputBuilder(QueryBuilder inputBuilder) {
        this.inputBuilder = inputBuilder;
    }

    public PagenationCondtion nextQueryCondion(SortOrder sortOrder) {
        PagenationCondtion queryCondtion = esPagenation.createCondtion(
                sortOrder == SortOrder.DESC ? inputPageNo - maxQueryNum / inputPageSize : inputPageNo + maxQueryNum / inputPageSize,
                getInputPageSize());
        queryCondtion.setPreCondtion(this);
        queryCondtion.setEsPagenation(esPagenation);
        queryCondtion.setInputBuilder(inputBuilder);
        queryCondtion.setMaxQueryNum(maxQueryNum);
        return queryCondtion;
    }

    public PagenationCondtionEntity calcPagenationCondtionEntity() {
        esPagenation.beforeQuery(inputPageSize, inputBuilder);
        /*默认创建首页查询实体*/
        PagenationCondtionEntity entity = new PagenationCondtionEntity(inputPageNo, inputPageSize);
        entity.setInputBuilder(inputBuilder);
        if (inputPageNo == 1) {
            return entity;
        }
        IPageCache pageCache = esPagenation.getPageCache();
        /*非首页查询时需设置条件实体内容*/
        if (pageCache.isHaveQuery(this.inputPageNo)) {
            /**
             * 如果已经查询过，则直接根据内存中保存的ID进行条件查询
             */
            PagenationIdPair pagenationIds = pageCache.getIdPairFromQueryPage(this.inputPageNo);
            entity.setRangeQueryBuilder(QueryBuilders.rangeQuery(ESPagenation.ID)
                    .gte(pagenationIds.getSmallId())
                    .lte(pagenationIds.getBigId()));
            entity.setQueryFromValue(0);
        } else if (esPagenation.getLastPageNum() != null && inputPageNo == esPagenation.getLastPageNum()) {
            //当输入的页码正好为最后一页，则直接按默认查询条件的倒序查询，若默认为倒序查询，则顺序查询
            entity.setQueryPageSize(esPagenation.getTotal() % inputPageSize > 0 ? (int) (esPagenation.getTotal() % inputPageSize) : inputPageSize);
            entity.setQueryFromValue(0);
            entity.setSortOrder(SortOrder.ASC);
        } else {
            PageNoGroup pageNoPair = pageCache.getClosestFromTarget(this.inputPageNo);
            if (!pageCache.isFirstQuery()) {
                //这种情况为不是第一次查询，第一次查询不需要id条件
                RangeQueryBuilder builder = QueryBuilders.rangeQuery(ESPagenation.ID);
                builder = pageCache.isHaveQuery(pageNoPair.getEndPageNo()) ? builder.gt(pageCache.getIdPairFromQueryPage(pageNoPair.getEndPageNo()).getBigId()) : builder;
                entity.setRangeQueryBuilder(pageCache.isHaveQuery(pageNoPair.getStartPageNo()) ? builder.lt(pageCache.getIdPairFromQueryPage(pageNoPair.getStartPageNo()).getSmallId()) : builder);

                if (pageNoPair.onlyHasStartPageNoOfClosest()) {
                    entity.setQueryFromValue((inputPageNo - pageNoPair.getStartPageNo() - 1) * inputPageSize);
                } else if (pageNoPair.onlyHasEndPageNoOfClosest()) {
                    /*如果输入的页面离结束页比较近*/
                    entity.setQueryFromValue((pageNoPair.getEndPageNo() - inputPageNo - 1) * inputPageSize);
                    entity.setSortOrder(SortOrder.ASC);
                } else {
                    /*如果输入的页面离起始页比较近*/
                    if (pageNoPair.getEndPageNo() - inputPageNo > inputPageNo - pageNoPair.getStartPageNo()) {
                        entity.setQueryFromValue((inputPageNo - pageNoPair.getStartPageNo() - 1) * inputPageSize);
                    } else {
                        /*如果输入的页面离结束页比较近*/
                        entity.setQueryFromValue((pageNoPair.getEndPageNo() - inputPageNo - 1) * inputPageSize);
                        entity.setSortOrder(SortOrder.ASC);
                    }
                }
            }
        }
        return entity;
    }
}