package net.dasnotsad.framework.kafka.producer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;

/**
 * kafka生产者工厂类
 *
 * @author liuliwei
 * @create 2018-12-18
 */

public class KafkaProducerFactory {

	private String kafkaServers;

	private Properties producerProperties;

	private volatile CloseSafeProducer<String, byte[]> producer;

	public KafkaProducerFactory(String kafkaServers, Properties producerProperties) {
		this.kafkaServers = kafkaServers;
		this.producerProperties = producerProperties;
	}

	public String getNodes() {
		return kafkaServers;
	}

	/**
	 * 创建生产者
	 */
	public CloseSafeProducer<String, byte[]> getProducer(Long lingerMs, Long batchSize, String compressionType, Integer acks, Long bufferMemory) {
		if (this.producer == null) {// 以懒加载的方式创建
			synchronized (this) {
				if (this.producer == null) {
                    producerProperties.put("linger.ms", String.valueOf(lingerMs));
                    producerProperties.put("batch.size", String.valueOf(batchSize));
                    producerProperties.put("compression.type", compressionType);
                    producerProperties.put("acks", String.valueOf(acks));
                    producerProperties.put("buffer.memory", String.valueOf(bufferMemory));
					this.producer = new CloseSafeProducer<>(new KafkaProducer<>(p(kafkaServers, producerProperties)));
				}
			}
		}
		return this.producer;
	}

    private class CloseSafeProducer<K, V> implements Producer<K, V> {

        private final Producer<K, V> delegate;

        CloseSafeProducer(Producer<K, V> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Future<RecordMetadata> send(ProducerRecord<K, V> record) {
            return this.delegate.send(record);
        }

        @Override
        public Future<RecordMetadata> send(ProducerRecord<K, V> record, Callback callback) {
            return this.delegate.send(record, callback);
        }

        @Override
        public void flush() {
            this.delegate.flush();
        }

        @Override
        public List<PartitionInfo> partitionsFor(String topic) {
            return this.delegate.partitionsFor(topic);
        }

        @Override
        public Map<MetricName, ? extends Metric> metrics() {
            return this.delegate.metrics();
        }

        @Override
        public void close() {
        }

        @Override
        public void close(Duration duration) {
        }

        @Override
        public void initTransactions() {
        }

        @Override
        public void beginTransaction() throws ProducerFencedException {
        }

        @Override
        public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, String consumerGroupId)
                throws ProducerFencedException {
        }

        @Override
        public void commitTransaction() throws ProducerFencedException {
        }

        @Override
        public void abortTransaction() throws ProducerFencedException {
        }
    }
    
	private Properties p(String kafkaServers, Properties properties) {
		Properties producerProperties = new Properties();
        producerProperties.putAll(properties);
		producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
		return producerProperties;
	}
}