package net.dasnotsad.framework.tac.data.metadata;


import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * @Description: 分页接口
 * @Author Created by yan.x on 2020-07-22 .
 **/
public interface IPage<T> extends Serializable {

    /**
     * 分页记录列表
     *
     * @return 分页对象记录列表
     */
    java.util.List<T> getRecords();

    /**
     * 设置分页记录列表
     */
    IPage<T> setRecords(java.util.List<T> records);

    /**
     * 当前页，默认 1
     *
     * @return 当前页
     */
    int getCurrentPage();

    /**
     * 设置当前页
     */
    IPage<T> setCurrentPage(int currentPage);

    /**
     * 获取每页显示条数
     *
     * @return 每页显示条数
     */
    int getPageSize();

    /**
     * 设置每页显示条数
     */
    IPage<T> setPageSize(int pageSize);

    /**
     * 当前满足条件总行数
     *
     * @return 总条数
     */
    long getTotalCount();

    /**
     * 设置当前满足条件总行数
     */
    IPage<T> setTotalCount(long totalCount);

    /**
     * 获取总页数
     */
    default long getTotalPage() {
        if (getPageSize() == 0) {
            return 0L;
        }
        long pages = getTotalCount() / getPageSize();
        if (getTotalCount() % getPageSize() != 0) {
            pages++;
        }
        return pages;
    }

    /**
     * 设置总页数
     */
    default IPage<T> setTotalPage(long totalPage) {
        return this;
    }

    default boolean isSearchCount() {
        return true;
    }

    /**
     * IPage 的泛型转换
     *
     * @param mapper 转换函数
     * @param <R>    转换后的泛型
     * @return 转换泛型后的 IPage
     */
    @SuppressWarnings("unchecked")
    default <R> IPage<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getRecords().stream().map(mapper).collect(toList());
        return ((IPage<R>) this).setRecords(collect);
    }
}
