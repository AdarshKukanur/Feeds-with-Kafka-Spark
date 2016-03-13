package kafkaRss;
import java.util.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
public class Consumer implements Runnable{
	private final KafkaConsumer<String, String> consumer;
	private final String topic;
	public Consumer(String topic) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "test");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumer = new KafkaConsumer<>(props);
		this.topic = topic;
		consumer.subscribe(Arrays.asList(this.topic));	
		
		
	}
	public void run() {
		System.out.println("Consumer Thread");
        try {
        	  while (true && !Thread.interrupted()) {
        	    ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
        	    for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
                }
        	  }
        	} catch (WakeupException e) {
        	  // ignore for shutdown
        	} finally {
        	  consumer.close();
        	}
	}
	
	 public void shutdown() {
		    consumer.wakeup();
		  }


}
