package com.kafka.top;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class TopProducer {
	
	private Logger log = LoggerFactory.getLogger(TopProducer.class);
	
	private String metadataBrokerList;
	
	private Producer<String, String> producer;
	
	
	public TopProducer(String metadataBrokerList) {
		super();
		
		if(StringUtils.isEmpty(metadataBrokerList)) {
			String message = "metadataBrokerList 不可为空";
			throw new RuntimeException(message);
		}
		
		this.metadataBrokerList = metadataBrokerList;
		
		// 设置配置属性
		Properties props = new Properties();
		props.put("metadata.broker.list", metadataBrokerList);
		props.put("serializer.class", "kafka.serizlizer.StringEncoder");
		props.put("key.serizlizer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");
		props.put("queue.buffering.max.ms", "5000");
		props.put("queue.buffering.max.messages", "30000");
		props.put("queue.enqueue.timeout.ms", "-1");
		props.put("batch.num.messages", "1");
		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String, String>(config);
	}
	
	
	/**
	 * 单条插入队列
	 * @param topic
	 * @param msg
	 * @return
	 */
	public String send(String topic, String msg) {
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, msg);
		producer.send(data);
		return "ok";
	}
	
}
