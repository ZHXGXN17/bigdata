package com.kafka.pro;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kafka.entity.Category;

public class ProDemo {
	
	public static void main(String[] args) {
		Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.126.129:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        //生产者发送消息 
        String topic = "test";
        Producer<String, String> procuder = new KafkaProducer<String,String>(props);
        for (int i = 1; i <= 100; i++) {
//            String value = "value_" + i;
            ProducerRecord<String, String> msg = new ProducerRecord<String, String>(topic, read());
            procuder.send(msg);
        }
        //列出topic的相关信息
        List<PartitionInfo> partitions = new ArrayList<PartitionInfo>() ;
        partitions = procuder.partitionsFor(topic);
        for(PartitionInfo p:partitions)
        {
            System.out.println(p);
        }

        System.out.println("send message over.");
        procuder.close(100,TimeUnit.MILLISECONDS);
	}
	
	
	/**
	 * 基于@Expose注解,不输出业务字段
	 * 
	 */
	public static String read() {
		List<Category> list = new ArrayList<Category>();
		Category category = new Category();
		category.setId(100);
		category.setName("笔记本");
		list.add(category);
		
		category.setId(101);
		category.setName("台式机");
		list.add(category);
		
		Category categoryList = new Category();
		categoryList.setId(1);
		categoryList.setName("电脑");
		categoryList.setChildren(list);
		
		Gson gson = new GsonBuilder()
				    .excludeFieldsWithoutExposeAnnotation()
				    .setPrettyPrinting()
				    .create();
//		System.out.println(gson.toJson(categoryList));
		return gson.toJson(categoryList);
	}
	
	
	
}
