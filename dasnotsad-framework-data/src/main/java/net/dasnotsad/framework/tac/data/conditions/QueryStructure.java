package net.dasnotsad.framework.tac.data.conditions;

import net.dasnotsad.framework.tac.data.metadata.Pageable;

import java.io.Serializable;

/**
 * @Description: 查询构造器 (Query Builder
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public interface QueryStructure<Children, T, R> extends Serializable {
    /**
     * 默认分页大小,每页10条
     */
    int DEFAULT_PAGE_SIZE = 10;

    /*---------------------------------------------[ 分页 ]---------------------------------------------*/

    /**
     * 获取分页对象
     *
     * @return
     */
    Pageable getPageList();

    /**
     * 设置分页对象
     *
     * @param pageList
     * @param <T>
     * @return
     */
    <T extends QueryStructure> T setPageList(Pageable pageList);

    /*---------------------------------------------[ 排序 ]---------------------------------------------*/

    /*---------------------------------------------[ 查询条件 ]---------------------------------------------*/


}
