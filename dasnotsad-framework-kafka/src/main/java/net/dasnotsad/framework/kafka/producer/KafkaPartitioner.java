package net.dasnotsad.framework.kafka.producer;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

/**
 * kafka生产者自定义分区器
 *
 * @author liuliwei
 * @create 2018-12-28
 */
public class KafkaPartitioner implements Partitioner {

    @Override
    public void configure(Map<String, ?> configs) {
        // TODO Auto-generated method stub
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
    }
}