package net.dasnotsad.framework.tac.data.query;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * 查询条件封装
 * 嵌套支持
 *
 * @author liuliwei
 * @create 2020-08-28
 */
public interface Nested<Param, Children> extends Serializable {

    /**
     * 关联——且
     *
     * @param consumer 查询条件
     */
    Children and(Consumer<Param> consumer);

    /**
     * 关联——或
     *
     * @param consumer 查询条件
     */
    Children or(Consumer<Param> consumer);
}
