package net.dasnotsad.framework.tac.data.query;

import java.io.Serializable;

/**
 * 查询条数限制
 *
 * @author liuliwei
 * @create 2020-08-28
 */
public interface Limit<R> extends Serializable {

    /**
     * skip数
     *
     * @param val 数量
     */
    void from(long val);

    /**
     * 返回条数限制
     *
     * @param val 数量
     */
    void size(int val);
}
