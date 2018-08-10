package com.kafka.day2;

public class ClientConsumer {
	public static void main(String[] args) {
		Consumer consumer1 = new Consumer(KafkaProperties.TOPIC1, true );  
		
		consumer1.start(); 
	}
}
