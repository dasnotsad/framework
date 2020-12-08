package net.dasnotsad.framework.tac.elasticsearch.repository;

import net.dasnotsad.framework.tac.elasticsearch.AbstractElasticSearchRepository;

/**
 * @Description: TODO
 * @Author Created by HOT SUN on 2020/7/22 .
 **/
public class SimpleElasticSearchRepository<T> extends AbstractElasticSearchRepository<T, String> {


    public SimpleElasticSearchRepository() {
        super();
    }

    @Override
    protected String stringIdRepresentation(String id) {
        return id;
    }
}
