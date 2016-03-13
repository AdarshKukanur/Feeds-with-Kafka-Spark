package kafkaExamples;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaMainExample {
	public static void main(String[] args)
	{
		
		ProducerExample producerThread  = new ProducerExample("test1", true);
		ConsumerExample consumerThread = new ConsumerExample("test1");
		
		//Kafka Consumer working with console producer
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.submit(consumerThread);	
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		      consumerThread.shutdown();
		      executor.shutdown();
		      try {
		        executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
		      } catch (InterruptedException e) {
		        e.printStackTrace();
		      }
		    }
		  });
		//Producer not working
				producerThread.run();
				producerThread.close();
		
	}
}
