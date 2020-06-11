package net.dasnotsad.framework.elasticsearch.core.page;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.SearchHit;

import com.alibaba.fastjson.JSON;

public class PageResult<I> {

    /**
     * 当前页号
     */
    private int pageNumber;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 查询总数
     */
    private long total;

    /**
     * 结果列表
     */
    private List<I> list;

    public PageResult() {
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalPage() {
        // 总页数 = （总记录数 + 每页数据大小 - 1） / 每页数据大小
        return (total + pageSize - 1) / pageSize;
    }

    public List<I> getList() {
        return list;
    }

    public void setList(List<I> list) {
        this.list = list;
    }

    public void setList(SearchHit[] hits, Class<I> clazz) {
        final int len = hits.length;
        this.list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            list.add(JSON.parseObject(hits[i].getSourceAsString(), clazz));
        }
    }
}