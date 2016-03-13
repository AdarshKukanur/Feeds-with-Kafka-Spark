package kafkaExamples;
import java.util.*;
import org.apache.kafka.clients.producer.*;
import java.util.concurrent.ExecutionException;
public class ProducerExample implements Runnable{

	private final KafkaProducer<String, String> producer;
	private final String topic;
	private final Boolean isAsync;

	public ProducerExample(String topic, Boolean isAsync) {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<>(props);
		this.topic = topic;
		this.isAsync = isAsync;
	}

	public void run() {

		int messageNo = 1;
		while (true && !Thread.interrupted()) {

			String messageStr = "Message_" + messageNo;

			try {
				producer.send(new ProducerRecord<String, String>(topic,
						Integer.toString(messageNo),
						messageStr),new DemoProducerCallback());
				System.out.println("Sent message: (" + messageNo + ", " + messageStr + ")");
				Thread.sleep(1000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			++messageNo;
		}
	}
	
	public void close() {
        producer.close();
    }
	
	private class DemoProducerCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                System.out.println("Error producing to topic " + recordMetadata.topic());
                e.printStackTrace();
            }
        }
    }

}
