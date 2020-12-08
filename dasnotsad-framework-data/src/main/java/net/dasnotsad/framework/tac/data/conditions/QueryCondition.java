package net.dasnotsad.framework.tac.data.conditions;

import net.dasnotsad.framework.tac.data.metadata.Pageable;

/**
 * @Description: 查询条件构造器
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public class QueryCondition<T> extends AbstractCondition<T, String, QueryCondition<T>>
        implements QueryStructure<QueryCondition<T>, T, String> {


    @Override
    public Pageable getPageList() {
        return null;
    }

    @Override
    public <T1 extends QueryStructure> T1 setPageList(Pageable pageList) {
        return null;
    }
}
