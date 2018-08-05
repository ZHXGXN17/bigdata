package com.kafka.top;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class TopReceiver {
	
	private Logger log = LoggerFactory.getLogger(TopReceiver.class);
	
	private String zookeeperConnect;
	
	private String groupId;
	
	private String topic;
	
	private TopConsumer topConsumer;
	
	public TopReceiver(String zookeeperConnect, String groupId, String topic, TopConsumer topConsumer) {
		super();
		if(StringUtils.isEmpty(zookeeperConnect)) {
			String message = "zookeeperConnnect 不可以为空";
			log.error(message);
			throw new RuntimeException(message);
		}
		if(StringUtils.isEmpty(groupId)) {
			String message = "groupId 不可以为空";
			log.error(message);
			throw new RuntimeException(message);
		}
		if(StringUtils.isEmpty(topic)) {
			String message = "topic 不可以为空";
			log.error(message);
			throw new RuntimeException(message);
		}
		if(StringUtils.isEmpty(topConsumer)) {
			String message = "topConsumer 不可以为空";
			log.error(message);
			throw new RuntimeException(message);
		}
		
		this.zookeeperConnect = zookeeperConnect;
		this.groupId = groupId;
		this.topic = topic;
		this.topConsumer = topConsumer;
		log.info("kafka topConsumer 创建完成:groupId:{}, topic:{}, zookeeperConnect:{}", groupId, topic, zookeeperConnect);
		receive();
	}
	
	
	private void receive() {
		Properties props = new Properties();
		props.put("zookeeper.connect", zookeeperConnect);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "14000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "14000");
		ConsumerConfig config = new ConsumerConfig(props);
		ConsumerConnector cc = Consumer.createJavaConsumerConnector(config);
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		// 目前每个topic都是2个分区
	    topicCountMap.put(topic,2);
	    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = cc.createMessageStreams(topicCountMap);
	       List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
	       for (final KafkaStream<byte[], byte[]> stream : streams) {
	    	   new Thread(){
	        		public void run(){
	        			ConsumerIterator<byte[], byte[]> it = stream.iterator();
	    		        while(it.hasNext()){
	    		        	String msg = new String(it.next().message());
	    		        	try{
	    		        	 topConsumer.dealMsg(msg);
	    		        	}catch(Exception e){
	    		        		log.error("kafka vkoConsumer topic:{} 收到消息：{} 消费异常 xxxxxxxxxxxxxxxxxx", topic, msg,e);
	    		        	}
	    		        	log.info("kafka vkoConsumer topic:{} 收到消息：{}", topic, msg);
	    		        }
	        		}
	        	}.start();
	        	log.info("kafka vkoConsumer 启动完成：groupId：{},topic:{},zookeeperConnect:{}",groupId, topic, zookeeperConnect);
	        }
	        log.info("kafka vkoConsumer 准备接收消息：groupId：{},topic:{},zookeeperConnect:{}",groupId, topic, zookeeperConnect);
	}
	
	
}
