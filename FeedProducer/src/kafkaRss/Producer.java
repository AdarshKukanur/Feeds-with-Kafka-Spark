package kafkaRss;
import java.util.*;
import org.apache.kafka.clients.producer.*;
import com.rometools.rome.feed.*;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.*;

public class Producer implements Runnable{

	private final KafkaProducer<String, String> producer;
	private final String topic;
	private final Boolean isAsync;
	private MyFeeds rss;
	private ArrayList<String> feedUrlList;

	public Producer(String topic, Boolean isAsync, ArrayList<String> feedUrlList) {
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
		this.rss = new MyFeeds();
		this.feedUrlList = new ArrayList<String>(feedUrlList);
	}

	public void run() {

		int messageNo = 1;
		int i = 0;
		while (i < feedUrlList.size() && !Thread.interrupted()) {
			
			SyndFeed syndFeed = rss.getFeeds(feedUrlList.get(i));
			for(SyndEntry entry : syndFeed.getEntries())
			{
				try {
					producer.send(new ProducerRecord<String, String>(topic,
							Integer.toString(messageNo),
							entry.getTitle()),new DemoProducerCallback());
					System.out.println("Sent message: (" + messageNo + ", " + entry.getTitle() + ")");
					Thread.sleep(100);

				} catch (Exception e) {
					e.printStackTrace();
				}
				++messageNo;
			}
			++i;
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
