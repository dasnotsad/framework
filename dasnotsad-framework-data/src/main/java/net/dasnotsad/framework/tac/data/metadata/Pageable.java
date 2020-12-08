package net.dasnotsad.framework.tac.data.metadata;


import java.util.Collections;
import java.util.List;

/**
 * @Description 分页查询对象
 * @Version 1.0.0
 * @Author Created by yan.x on 2019-04-30 .
 **/
public class Pageable<T> implements IPage<T> {

    private static final long serialVersionUID = 8545996863226528798L;

    /**
     * 当前页
     */
    private int currentPage = 1;

    /**
     * 每页数据
     */
    private int pageSize = 10;

    /**
     * 总页数
     */
    private long totalPage;

    /**
     * 总数据量
     */
    private long totalCount = 0;

    /**
     * 所有集合
     */
    private java.util.List<T> records = Collections.emptyList();

    /**
     * 是否进行 count 查询
     */
    private boolean isSearchCount = true;


    public static <T> Pageable<T> build() {
        return new Pageable<T>();
    }

    public static <T> Pageable<T> build(int currentPage, int pageSize) {
        return new Pageable<T>(currentPage, pageSize);
    }

    public static <T> Pageable<T> build(int currentPage, int pageSize, long totalCount) {
        return new Pageable<T>(currentPage, pageSize, totalCount);
    }

    public static <T> Pageable<T> build(int currentPage, int pageSize, java.util.List<T> records) {
        return new Pageable<T>(currentPage, pageSize, records);
    }

    public static <T> Pageable<T> build(int currentPage, int pageSize, long totalCount, java.util.List<T> records) {
        return new Pageable<T>(currentPage, pageSize, totalCount, records);
    }


    @Override
    public List<T> getRecords() {
        return records;
    }

    @Override
    public IPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public int getCurrentPage() {
        return this.currentPage;
    }

    @Override
    public IPage<T> setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public IPage<T> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public long getTotalCount() {
        return this.totalCount;
    }

    @Override
    public IPage<T> setTotalCount(long totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    @Override
    public boolean isSearchCount() {
        if (totalCount < 0) {
            return false;
        }
        return isSearchCount;
    }

    public Pageable<T> setSearchCount(boolean isSearchCount) {
        this.isSearchCount = isSearchCount;
        return this;
    }


    public Pageable() {
    }

    /**
     * 分页
     *
     * @param pageSize    每页记录数
     * @param currentPage 当前页数
     */
    public Pageable(int currentPage, int pageSize) {
        this(currentPage, pageSize, 0);
    }

    /**
     * 分页
     *
     * @param pageSize      每页记录数
     * @param currentPage   当前页数
     * @param isSearchCount 是否进行 count 查询
     */
    public Pageable(int currentPage, int pageSize, boolean isSearchCount) {
        this(currentPage, pageSize, 0, isSearchCount);
    }

    /**
     * 分页
     *
     * @param currentPage 当前页数
     * @param pageSize    每页记录数
     * @param totalCount  总记录数
     */
    public Pageable(int currentPage, int pageSize, long totalCount) {
        this(currentPage, pageSize, totalCount, true);
    }

    /**
     * 分页
     *
     * @param currentPage   当前页数
     * @param pageSize      每页记录数
     * @param totalCount    总记录数
     * @param isSearchCount 是否进行 count 查询
     */
    public Pageable(int currentPage, int pageSize, long totalCount, boolean isSearchCount) {
        if (currentPage > 1) {
            this.currentPage = currentPage;
        }
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
        this.totalCount = totalCount;
        this.isSearchCount = isSearchCount;
        init();
    }

    /**
     * 分页
     *
     * @param currentPage 当前页数
     * @param pageSize    每页记录数
     * @param records     列表数据
     */
    public Pageable(int currentPage, int pageSize, java.util.List<T> records) {
        this(currentPage, pageSize, false);
        this.records = records;
    }

    /**
     * 分页
     *
     * @param currentPage 当前页数
     * @param pageSize    每页记录数
     * @param totalCount  总记录数
     * @param records     列表数据
     */
    public Pageable(int currentPage, int pageSize, long totalCount, java.util.List<T> records) {
        this(currentPage, pageSize, totalCount);
        this.records = records;
    }

    /**
     * 初始化计算分页
     */
    private void init() {
    }
}
