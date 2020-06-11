package net.dasnotsad.framework.kafka.consumer;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.dasnotsad.framework.kafka.annotation.KafkaListener;
import net.dasnotsad.framework.kafka.config.KafkaContainer;
import net.dasnotsad.framework.kafka.constant.KafkaConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @Author: liuliwei
 * @Date: 2018-12-21
 * @Description: kafka消费者注解实现
 */
@Slf4j
@Component
public class KafkaConsumerInit {

	@Autowired
	private KafkaContainer kafkaContainer;

	@Autowired
	private Environment environment;

	@Resource(name = "consumerProperties")
	private Properties consumerProperties;

	@Async
	public void consumeAsync(Object bean, Method method, KafkaListener kafkaListener) {
		String[] topics = kafkaListener.topicNames();

		if (topics == null || topics.length == 0) {
			if (StringUtils.isEmpty(kafkaListener.topicName())) {
				throw new RuntimeException("@KafkaListener topicName or topicNames must be setting");
			}
			topics = new String[] { kafkaListener.topicName() };
		}

		// 进行表达式转换
		Set<String> topicNames = new HashSet<>(topics.length);
		for (String topic : topics) {
			topicNames.add(convertExpression(topic));
		}

		int threadNum = kafkaListener.threadNum() < 1 ? 1 : kafkaListener.threadNum();
		int consumerNum = kafkaListener.consumerNum() < 1 ? 1 : kafkaListener.consumerNum();

		ExecutorService consumerExecutor = new ThreadPoolExecutor(consumerNum, consumerNum, 0L, TimeUnit.MILLISECONDS,
				new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
		while (--consumerNum >= 0) {
			consumerExecutor.submit(new InternalConsumer(bean, method, topicNames, threadNum, kafkaListener));
		}
	}

	class InternalConsumer implements Runnable {
		private Object bean;
		private Method method;
		private Set<String> topics;
		private int threadNum;
		private KafkaListener kafkaListener;

		public InternalConsumer(Object bean, Method method, Set<String> topics, int threadNum, KafkaListener kafkaListener) {
			this.bean = bean;
			this.method = method;
			this.topics = topics;
			this.threadNum = threadNum;
			this.kafkaListener = kafkaListener;
		}

		@Override
		public void run() {
			ExecutorService internalExecutor = new ThreadPoolExecutor(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS,
					new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());

			int whichDataSource = kafkaListener.whichDataSource();
			String kafkaServers = kafkaContainer.get(whichDataSource).getNodes();

			String groupId = kafkaListener.broadcast()
					? convertExpression(kafkaListener.groupId()) + "_" + getRealIP()
					: convertExpression(kafkaListener.groupId());

			KafkaConsumer<String, byte[]> kafkaConsumer = new KafkaConsumer<>(c(kafkaServers, groupId));
			kafkaConsumer.subscribe(topics);
			try {
				for (;;) {
					ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(Duration.ofSeconds(1));
					if (!records.isEmpty()) {
						internalExecutor.submit(new InternalExecutor(bean, method, records));
					}
				}
			} finally {
				kafkaConsumer.close();
			}
		}
	}

	class InternalExecutor implements Runnable {

		private Object bean;
		private Method method;
		private ConsumerRecords<String, byte[]> records;

		public InternalExecutor(Object bean, Method method, ConsumerRecords<String, byte[]> records) {
			this.bean = bean;
			this.method = method;
			this.records = records;
		}

		@Override
		public void run() {
			try {
				Class<?> valueClass = method.getParameterTypes()[0];
				method.setAccessible(true);
				for (ConsumerRecord<String, byte[]> record : records) {
					String metadata = new String(record.value(), Charset.defaultCharset());
					method.invoke(bean, (valueClass == String.class) ? metadata : JSON.parseObject(metadata, valueClass));
				}
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private Properties c(String kafkaServers, String groupId) {
		Properties c = new Properties();
		c.putAll(consumerProperties);
		c.put(GROUP_ID_CONFIG, groupId);
		c.put(BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
		return c;
	}

	/**
	 * 将表达式替换为实际值
	 * 
	 * @param expressionStr Spel表达式
	 * @return
	 */
	private String convertExpression(String expressionStr) {
		int pindex = expressionStr.indexOf(KafkaConstant.EXPRESSION_PREFFIX);
		int sindex = expressionStr.indexOf(KafkaConstant.EXPRESSION_SUFFIX);
		if (pindex != -1 && sindex != -1) {
			StringBuilder builder = new StringBuilder();
			if (pindex > 0) {
				builder.append(expressionStr, 0, pindex);
			}
			String middle = expressionStr.substring(pindex + 2, sindex).trim();
			if (middle.length() != 0) {
				String value = environment.getProperty(middle);
				if (value == null || value.length() == 0) {
					log.error("Could not resolve placeholder '" + middle + "' in value " + expressionStr);
					throw new RuntimeException(
							"Could not resolve placeholder '" + middle + "' in value " + expressionStr);
				}
				builder.append(value.trim());
			}
			if (sindex != expressionStr.length()) {
				builder.append(expressionStr.substring(sindex + 1));
			}
			return builder.toString();
		}
		return expressionStr;
	}

	private String getRealIP() {
		String ip = "127.0.0.1";
		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			loop: while (netInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = netInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
						ip = address.getHostAddress();
						break loop;
					}
				}
			}
		} catch (SocketException e) {
			ip = "127.0.0.1";
		}
		return ip;
	}
}
