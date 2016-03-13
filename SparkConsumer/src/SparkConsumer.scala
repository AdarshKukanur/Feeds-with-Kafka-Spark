import org.apache.spark.{SparkConf};
import org.apache.spark.streaming.kafka._;
import org.apache.spark.streaming._;

object SparkConsumer extends App{
	val sparkConf = new SparkConf().setMaster("local[2]").setAppName("KafkaWordCount");
	val ssc = new StreamingContext(sparkConf, Seconds(2));
	ssc.checkpoint("checkpoint")
	val topics = "test";
	val topicMap = topics.split(",").map((_, 1)).toMap;
	print(topicMap);
	val titles = KafkaUtils.createStream(ssc, "localhost:2181", "test", topicMap).map(_._2);
	val words = titles.flatMap(_.split(" "));
	val wordCounts = words.map(x => (x, 1L))
	.reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2)
	.map{case (topic, count) => (count, topic)}
	.transform(_.sortByKey(false));
	wordCounts.print();

	ssc.start();
	ssc.awaitTermination();
}