package kafkaRss;

import java.io.IOException;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaMain{
	public static void main(String[] args) throws IOException
	{
		ArrayList<String> feedUrlList = new ArrayList<String>();
		String fileName = "feedurl.txt";
		
		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			stream.forEach(e -> feedUrlList.add(e));;

		} catch (IOException e) {
			e.printStackTrace();
		}

	Producer producerThread  = new Producer("test", true, feedUrlList);
	/*	Consumer consumerThread = new Consumer("test");

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
		});*/
	producerThread.run();
	producerThread.close();

}
}
