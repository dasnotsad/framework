package net.dasnotsad.framework.tac.data.query;

/**
 * 查询语句拼装第一层抽象
 * T：对象类型，对应表名（mysql）、集合名（mongoDB）、索引名（elasticsearch）
 * Children：方法返回，对应当前查询条件的封装对象
 * R：字段名，代表传入的lambda解析结果
 *
 * @author liuliwei
 * @create 2020-08-28
 */
public abstract class AbstractWrapper<T, R, Children extends AbstractWrapper<T, R, Children>>
        implements Compare<R, Children>, Nested<Children, Children>, Sort<R, Children>, Limit<R> {
}
