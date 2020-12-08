package net.dasnotsad.framework.tac.data.query;

import net.dasnotsad.framework.tac.data.enums.Nested;
import net.dasnotsad.framework.tac.data.enums.Query;
import net.dasnotsad.framework.tac.data.lambda.SFunction;
import net.dasnotsad.framework.tac.data.utils.LambdaUtil;

import java.util.List;
import java.util.Map;

/**
 * 查询语句拼装Lambda抽象
 *
 * T：对象类型，对应表名（mysql）、集合名（mongoDB）、索引名（elasticsearch）
 * Children：方法返回，对应当前查询条件的封装对象
 *
 * @author liuliwei
 * @create 2020-08-28
 */
public abstract class AbstractLambdaWrapper<T, Children extends AbstractLambdaWrapper<T, Children>>
        extends AbstractWrapper<T, SFunction<T, ?>, Children> {

    /**
     * 占位符
     */
    protected final Children typedThis = (Children) this;

    /**
     * 查询条件数组
     */
    protected List<QueryObj> queryList;

    /**
     * 逻辑连接条件
     */
    protected Map<Nested, AbstractWrapper> nestedMap;

    protected Class<T> entityClass;

    @Override
    public Children eq(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.eq);
    }

    @Override
    public Children ne(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.ne);
    }

    @Override
    public Children gt(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.gt);
    }

    @Override
    public Children gte(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.gte);
    }

    @Override
    public Children lt(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.lt);
    }

    @Override
    public Children lte(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.lte);
    }

    @Override
    public Children rightLike(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.rightLike);
    }

    @Override
    public Children leftLike(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.leftLike);
    }

    @Override
    public Children allLike(SFunction<T, ?> column, Object val) {
        return addQuery(column, val, Query.allLike);
    }

    private Children addQuery(SFunction<T, ?> column, Object val, Query query){
        queryList.add(QueryObj.build(query, LambdaUtil.convertToFieldName(column), val));
        return typedThis;
    }
}
