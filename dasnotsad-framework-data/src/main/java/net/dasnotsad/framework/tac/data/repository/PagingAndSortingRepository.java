package net.dasnotsad.framework.tac.data.repository;

import net.dasnotsad.framework.tac.data.metadata.Pageable;
import net.dasnotsad.framework.tac.data.metadata.IPage;

/**
 * @Description: 分页和排序管理器接口
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public interface PagingAndSortingRepository<T, ID> extends CrudRepository<T, ID> {

    <E extends IPage<T>> E selectPage(Pageable pageable);
}
