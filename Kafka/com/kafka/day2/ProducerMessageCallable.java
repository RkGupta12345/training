package com.kafka.day2;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerMessageCallable  implements Callback{

	private long startTime; 
	private int key; 
	private String message; 
	
	
	
	
	public ProducerMessageCallable(long startTime, int key, 
				String message) {
		super();
		this.startTime = startTime;
		this.key = key;
		this.message = message;
	}




	@Override
	public void onCompletion(RecordMetadata metadata, 
			Exception exception) {
		// when the message is sent 
		long elapsedTime = System.currentTimeMillis() - startTime; 
		
		if(metadata != null) {
			System.out.println("Message: " + message +
					" with Key " + key +
					", sent to partition:  " + metadata.partition() +
					", offset : " + metadata.offset() +
					", Time Take : " + elapsedTime +" ms");
		}else {
			exception.printStackTrace(); 
		}
		
		
		
	}

}
