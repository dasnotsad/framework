package net.dasnotsad.framework.tac.data.repository;

import java.util.Collection;

/**
 * @Description: CRUD管理器接口
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public interface CrudRepository<T, ID> {

    /**
     * 插入一条记录
     *
     * @param entity 实体对象
     */
    <S extends T> S insert(S entity);

    /**
     * 批量插入数据
     *
     * @param entities 实体对象
     */
    Collection<? extends T> insertBatch(Collection<? extends T> entities);

    /**
     * 异步插入一条记录
     *
     * @param entity 实体对象
     */
    <S extends T> void insertAsync(S entity);

    /**
     * 异步批量插入数据
     *
     * @param entities 实体对象
     */
    void insertBatchAsync(Collection<? extends T> entities);

    /**
     * 根据 ID 删除
     *
     * @param id 主键ID
     */
    void deleteById(ID id);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param ids 主键ID列表(不能为 null 以及 empty)
     */
    void deleteBatchIds(Iterable<ID> ids);

    /**
     * 批量删除
     *
     */
    void deleteAll();

    /**
     * 根据实体对象批量删除
     *
     * @param entities
     */
    void deleteBatch(Collection<? extends T> entities);

    /**
     * 根据 ID 修改
     *
     * @param entity 实体对象
     */
    <S extends T> void updateById(S entity);

    /**
     * 根据实体对象批量更新
     *
     * @param entities 实体对象
     */
    void updateBatch(Collection<? extends T> entities);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     */
    T selectById(ID id);

    /**
     * 批量查询
     */
    Iterable<T> selectBatch();

    /**
     * 查询（根据ID 批量查询）
     *
     * @param ids 主键ID列表(不能为 null 以及 empty)
     */
    Iterable<T> selectBatchIds(Iterable<ID> ids);

    /**
     * 查询数量
     *
     * @return
     */
    long selectCount();

    /**
     * 是否存在（根据ID 匹配）
     *
     * @param id 主键ID
     * @return
     */
    boolean existsById(ID id);

}
