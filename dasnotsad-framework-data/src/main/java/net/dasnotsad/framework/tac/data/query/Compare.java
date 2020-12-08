package net.dasnotsad.framework.tac.data.query;

import java.io.Serializable;

/**
 * 查询语句条件定义，用于规范指定对象类型
 *
 * @author liuliwei
 * @create 2020-08-07
 */
public interface Compare<R, Children> extends Serializable {

    /**
     * 匹配（等于 equal）
     *
     * @param column 字段
     * @param val 值
     */
    Children eq(R column, Object val);

    /**
     * 不匹配（不等于 not equal）
     *
     * @param column 字段
     * @param val 值
     */
    Children ne(R column, Object val);

    /**
     * 大于（greate than）
     *
     * @param column 字段
     * @param val 值
     */
    Children gt(R column, Object val);

    /**
     * 大于等于（greate than equal）
     *
     * @param column 字段
     * @param val 值
     */
    Children gte(R column, Object val);

    /**
     * 小于（less than）
     *
     * @param column 字段
     * @param val 值
     */
    Children lt(R column, Object val);

    /**
     * 小于等于（less than equal）
     *
     * @param column 字段
     * @param val 值
     */
    Children lte(R column, Object val);

    /**
     * 右模糊匹配
     *
     * @param column 字段
     * @param val 值
     */
    Children rightLike(R column, Object val);

    /**
     * 左模糊匹配
     *
     * @param column 字段
     * @param val 值
     */
    Children leftLike(R column, Object val);

    /**
     * 左右模糊匹配
     *
     * @param column 字段
     * @param val 值
     */
    Children allLike(R column, Object val);
}
