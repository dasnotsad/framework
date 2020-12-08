package net.dasnotsad.framework.tac.data.query;

import java.io.Serializable;

/**
 * 排序方式定义
 * 嵌套支持
 *
 * @author liuliwei
 * @create 2020-08-28
 */
public interface Sort<R, Children> extends Serializable {

    /**
     * 排序
     *
     * @param column 字段
     * @param val 排序方式
     */
    Children sort(R column, net.dasnotsad.framework.tac.data.enums.Sort val);
}
