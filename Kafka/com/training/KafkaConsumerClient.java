package com.training;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;


public class KafkaConsumerClient {
	public static void main(String[] args) {
		Properties properties = new Properties();

		// identify the kafka server details
		properties.setProperty("bootstrap.servers", 
				"localhost:9092");

		properties.setProperty("key.deserializer", 
				StringDeserializer.class.getName());

		properties.setProperty("value.deserializer", 
				StringDeserializer.class.getName());

		properties.setProperty("group.id", "FirstGroup"); 
		
		properties.setProperty("session.timeout.ms", "30000"); 
		properties.setProperty("auto.offset.reset", "earliest"); 
		
		
		KafkaConsumer<String, String> 
			kafkaConsumer = new KafkaConsumer
				<String, String>(properties);
		

		kafkaConsumer.subscribe(Arrays.asList("topicA"));
		
		while(true) {
			
			ConsumerRecords<String, String> 
				consumerRecords = kafkaConsumer.poll(1000); 
			
			System.out.println("Count Number of Messages Got " + 
					consumerRecords.count());	
			
			for(ConsumerRecord<String, String> temp : 
					consumerRecords) {
				
				System.out.println(
					"Key: " + temp.key() + 
					", Value: " + temp.value() +
					", Partition: " + temp.partition() +
					", Topic " + temp.topic() +
					", Time :" + new Date(temp.timestamp())
						);
				
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		
		
		
		
		
		
		
	}
}
