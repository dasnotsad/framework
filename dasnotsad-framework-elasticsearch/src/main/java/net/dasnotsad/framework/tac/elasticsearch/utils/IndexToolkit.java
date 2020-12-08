package net.dasnotsad.framework.tac.elasticsearch.utils;

import lombok.SneakyThrows;
import net.dasnotsad.framework.tac.elasticsearch.annotation.EsDocument;
import net.dasnotsad.framework.tac.elasticsearch.annotation.EsIdentify;
import net.dasnotsad.framework.tac.elasticsearch.metadata.MetaData;

import java.lang.reflect.Field;

/**
 * @Description: 索引信息操作工具包
 * @Author Created by yan.x on 2020-07-22 .
 **/
public class IndexToolkit {

    /**
     * 根据对象中的注解获取ID的字段值
     *
     * @param obj
     * @return
     */
    @SneakyThrows
    public static String getESId(Object obj) {
        Field f = DeclaredFieldsUtil.findAnnotation(obj.getClass(), EsIdentify.class);
        if(f != null){
            Object value = f.get(obj);
            return value.toString();
        }
        return null;
    }

    /**
     * 获取索引元数据：indexName、indexType、主分片、备份分片数的配置
     *
     * @param clazz
     * @return
     */
    public static MetaData getMetaData(Class<?> clazz) {
        String indexName;
        int shards, replicas;
        EsDocument annotation = clazz.getAnnotation(EsDocument.class);
        if (annotation != null) {
            indexName = annotation.indexName();
            shards = annotation.shards();
            replicas = annotation.replicas();
            MetaData metaData = new MetaData(indexName, shards, replicas);
            metaData.setSearchIndexNames(new String[]{indexName});
            metaData.setCreateIndex(annotation.createIndex());
            metaData.setWhichDataSource(annotation.whichDataSource());
            return metaData;
        }
        return null;
    }
}
