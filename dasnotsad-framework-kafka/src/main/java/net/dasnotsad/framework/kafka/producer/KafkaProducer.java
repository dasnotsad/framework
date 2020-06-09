package net.dasnotsad.framework.kafka.producer;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;
import net.dasnotsad.framework.kafka.msg.model.DelayMessage;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.dasnotsad.framework.kafka.config.KafkaContainer;
import net.dasnotsad.framework.kafka.msg.model.PushReturnType;
import org.springframework.util.StringUtils;

/**
 * kafka生产者操作类
 *
 * @author liuliwei
 * @create 2019-4-1
 * @modify 2019-11-7
 */
@Component
@Slf4j
public class KafkaProducer {

	private static final String KAFKA_DELAY_TOPIC = "wheel_time_delay_topic";

	public static final int DEFAULT_DATASOURCE = KafkaContainer.DEFAULT_DATASOURCE;

	private ThreadLocal<Integer> whichDataSource = ThreadLocal.withInitial(() -> DEFAULT_DATASOURCE);

	@Autowired
	private KafkaContainer kafkaContainer;

	@Value("${spring.application.name:}")
	private String applicationName;

	/**
	 * 设置默认数据源
	 * 
	 * @param whichDataSource
	 */
	public void setDefaultWhichDataSource(int whichDataSource) {
		this.whichDataSource.set(whichDataSource);
	}

	/**
	 * 获取数据源
	 * 
	 * @param nodes
	 * @return 数据源编号
	 */
	public int getWhichDataSource(String nodes) {
		return kafkaContainer.getWhichDataSource(nodes);
	}

	/**
	 * 同步发送一条消息
	 *
	 * @param topic           主题
	 * @param message         消息体
	 * @return success:成功 faild:失败
	 */
	public PushReturnType pushMsgSync(String topic, Object message) {
		return pushMsgSync(whichDataSource.get(), topic, null, message);
	}

	/**
	 * 同步发送一条消息
	 *
	 * @param topic           主题
	 * @param key             自定义key，若指定则PR会按照hasy(key)发送至对应Partition
	 * @param message         消息体
	 * @return success:成功 faild:失败
	 */
	public PushReturnType pushMsgSync(String topic, String key, Object message) {
		return pushMsgSync(whichDataSource.get(), topic, key, message);
	}

	/**
	 * 同步发送一条消息
	 *
	 * @param whichDataSource 数据源标识
	 * @param topic           主题
	 * @param key             自定义key，若指定则PR会按照hasy(key)发送至对应Partition
	 * @param message         消息体
	 * @return success:成功 faild:失败
	 */
	public PushReturnType pushMsgSync(int whichDataSource, String topic, String key, Object message) {
		KafkaProducerFactory producerFactory = kafkaContainer.get(whichDataSource);
		Producer<String, byte[]> producer = producerFactory.getProducer();
		Future<RecordMetadata> future = producer.send(createProducerRecord(producerFactory, topic, key, message), (metadata, exception) -> {
			try {
				if (exception != null) {
					log.error("**********kafka consumer handler cause exception: {}", exception.getMessage(), exception);
					exception.printStackTrace();
				}
			} finally {
				producer.close();
			}
		});
		producer.flush();
		try {
			/* RecordMetadata res = */future.get();// 等待返回
			return PushReturnType.success;
		} catch (InterruptedException | ExecutionException e) {
			log.error("**********kafka consumer handler cause exception: {}", e.getMessage(), e);
			e.printStackTrace();
			return PushReturnType.faild;
		}
	}

	/**
	 * 异步发送一条消息
	 *
	 * @param topic       主题
	 * @param message     消息体
	 * @param callBack    异步的callBack方法
	 */
	public void pushMsgAsync(String topic, Object message, ProducerAsyncHandler callBack) {
		pushMsgAsync(whichDataSource.get(), topic, null, message, callBack);
	}

	/**
	 * 异步发送一条消息
	 *
	 * @param topic       主题
	 * @param key         自定义key，若指定则PR会按照hasy(key)发送至对应Partition
	 * @param message     消息体
	 * @param callBack    异步的callBack方法
	 */
	public void pushMsgAsync(String topic, String key, Object message, ProducerAsyncHandler callBack) {
		pushMsgAsync(whichDataSource.get(), topic, key, message, callBack);
	}

	/**
	 * 异步发送一条消息
	 *
	 * @param whichDataSource 数据源标识
	 * @param topic           主题
	 * @param key             自定义key，若指定则PR会按照hasy(key)发送至对应Partition
	 * @param message         消息体
	 * @param callBack        异步的callBack方法
	 */
	public void pushMsgAsync(int whichDataSource, String topic, String key, Object message, ProducerAsyncHandler callBack) {
		KafkaProducerFactory producerFactory = kafkaContainer.get(whichDataSource);
		Producer<String, byte[]> producer = producerFactory.getProducer();
		producer.send(createProducerRecord(producerFactory, topic, key, message), (metadata, exception) -> {
			try {
				if (exception != null) {
					log.error("**********kafka consumer handler cause exception: {}", exception);
					exception.printStackTrace();
				}
				if (callBack != null)
					callBack.onCompletion(metadata, exception);
			} finally {
				producer.close();
			}
		});
		producer.flush();
	}

	/**
	 * 延迟消息投递（同步）
	 *
	 * @param topic       主题
	 * @param message     消息体
	 * @param delayTime   延迟时间（秒）
	 * @return
	 */
	public PushReturnType pushDelayMsgSync(String topic, Object message, long delayTime) {
		return pushDelayMsgSync(topic, message, delayTime, kafkaContainer.get(whichDataSource.get()).getNodes());
	}

	/**
	 * 延迟消息投递（同步）
	 *
	 * @param topic       主题
	 * @param message     消息体
	 * @param delayTime   延迟时间（秒）
	 * @param nodes       连接地址（数据源）
	 * @return
	 */
	public PushReturnType pushDelayMsgSync(String topic, Object message, long delayTime, String nodes) {
		return pushDelayMsgSync(whichDataSource.get(), topic, message, delayTime, nodes);
	}

	/**
	 * 延迟消息投递（同步）
	 *
	 * @param whichDataSource 数据源标识
	 * @param topic       主题
	 * @param message     消息体
	 * @param delayTime   延迟时间（秒）
	 * @param nodes       连接地址（数据源）
	 * @return
	 */
	public PushReturnType pushDelayMsgSync(int whichDataSource, String topic, Object message, long delayTime, String nodes) {
		DelayMessage model = new DelayMessage();
		model.setTopic(topic);
		model.setSys_code(applicationName);
		model.setData(message);
		model.setDelay_time(System.currentTimeMillis() + (delayTime * 1000));
		model.setNodes(StringUtils.isEmpty(nodes) ? kafkaContainer.get(whichDataSource).getNodes(): nodes);
		return pushMsgSync(whichDataSource, KAFKA_DELAY_TOPIC, null, model);
	}

	/**
	 * 延迟消息投递（异步）
	 *
	 * @param topic       主题
	 * @param message     消息体
	 * @param delayTime   延迟时间（秒）
	 * @param callBack 回调
	 */
	public void pushDelayMsgAsync(String topic, Object message, long delayTime, ProducerAsyncHandler callBack){
		pushDelayMsgAsync(topic, message, delayTime, kafkaContainer.get(whichDataSource.get()).getNodes(), callBack);
	}

	/**
	 * 延迟消息投递（异步）
	 *
	 * @param topic       主题
	 * @param message     消息体
	 * @param delayTime   延迟时间（秒）
	 * @param nodes       连接地址（数据源）
	 * @param callBack 回调
	 */
	public void pushDelayMsgAsync(String topic, Object message, long delayTime, String nodes, ProducerAsyncHandler callBack){
		pushDelayMsgAsync(whichDataSource.get(), topic, message, delayTime, nodes, callBack);
	}

	/**
	 * 延迟消息投递（异步）
	 *
	 * @param whichDataSource 数据源标识
	 * @param topic       主题
	 * @param message     消息体
	 * @param delayTime   延迟时间（秒）
	 * @param nodes       连接地址（数据源）
	 * @param callBack 回调
	 */
	public void pushDelayMsgAsync(int whichDataSource, String topic, Object message, long delayTime, String nodes, ProducerAsyncHandler callBack){
		DelayMessage model = new DelayMessage();
		model.setTopic(topic);
		model.setSys_code(applicationName);
		model.setData(message);
		model.setDelay_time(System.currentTimeMillis() + (delayTime * 1000));
		model.setNodes(StringUtils.isEmpty(nodes) ? kafkaContainer.get(whichDataSource).getNodes() : nodes);
		pushMsgAsync(whichDataSource, KAFKA_DELAY_TOPIC, null, model, callBack);
	}

	//创建kafka消息
	private ProducerRecord<String, byte[]> createProducerRecord(KafkaProducerFactory producerFactory, String topic, String key, Object message){
		ProducerRecord<String, byte[]> producerRecord;
		if (message instanceof String)
			producerRecord = new ProducerRecord<>(topic, key==null ? (String)message : key,
					((String) message).getBytes(Charset.defaultCharset()));
		else if(message instanceof DelayMessage){//延迟队列特殊处理
			DelayMessage delayMsg = (DelayMessage)message;
			if(StringUtils.isEmpty(delayMsg.getNodes()))
				delayMsg.setNodes(producerFactory.getNodes());//默认取当前数据源
			producerRecord = new ProducerRecord<>(topic, JSONObject.toJSONString(delayMsg),
					JSON.toJSONBytes(delayMsg));
		}else
			producerRecord = new ProducerRecord<>(topic, JSONObject.toJSONString(message),
					JSON.toJSONBytes(message));
		return producerRecord;
	}
}