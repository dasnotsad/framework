package net.dasnotsad.framework.tac.elasticsearch.metadata;

import lombok.*;
import net.dasnotsad.framework.tac.elasticsearch.ESTemplate;

/**
 * @Description: 元数据载体类
 * @Author Created by yan.x on 2020-07-22 .
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaData {
    /**
     * 索引名称
     */
    private String indexName = "";

    /**
     * 检索时的索引名称
     */
    private String[] searchIndexNames;

    // 主分片数量
    private int shards;

    // 备份分片数量
    private int replicas;

    private String analyzer;

    private boolean createIndex = false;

    private int whichDataSource = ESTemplate.DEFAULT_DATASOURCE;

    public MetaData(String indexName) {
        this.indexName = indexName;
    }

    public MetaData(int shards, int replicas) {
        this.shards = shards;
        this.replicas = replicas;
    }

    public MetaData(String indexName, int shards, int replicas) {
        this.indexName = indexName;
        this.shards = shards;
        this.replicas = replicas;
    }
}