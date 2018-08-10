package com.kafka.day2;

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import kafka.utils.ShutdownableThread;

public class Consumer extends ShutdownableThread{

	private KafkaConsumer<Integer, String> consumer ; 
	private String topic;
	
	public Consumer(String name, boolean isInterruptible) {
	
		super(name, isInterruptible);


		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers",
				KafkaProperties.KAFKA_SERVER_URL + 
				":" + KafkaProperties.KAFKA_SERVER_PORT);

		properties.setProperty("key.deserializer", 
				IntegerDeserializer.class.getName());
		properties.setProperty("value.deserializer", 
				StringDeserializer.class.getName()); 
		
		properties.setProperty("request.timeout.ms", "120000"); 
		
//		properties.setProperty("group.id", 
//				"MyGroup");
	
		properties.setProperty("session.timeout.ms", 
				"100000"); 
		
		properties.setProperty("enable.auto.commit", "true"); 
		
		
		this.topic = name; 
		consumer = new KafkaConsumer<>(properties); 
		
	}

	@Override
	public void doWork() {
		System.out.println("in do work ");
		consumer.subscribe(Collections.singleton(this.topic));
		System.out.println("1");
		ConsumerRecords<Integer, String> records = 
				consumer.poll(1000); 
		System.out.println("2");
		for(ConsumerRecord<Integer,String> record : records) {
			System.out.println("Received Message Key -> " + 
					record.key() +", Message " + 
					record.value() +", @ offset : " + 
					record.offset() +", Partition : " + 
					record.partition());
		}
	}

	@Override
	public boolean isInterruptible() {
		// TODO Auto-generated method stub
		return false; 
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return this.topic; 
	}

	
	
	
}









