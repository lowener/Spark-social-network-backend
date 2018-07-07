package socialMedia.backend

import scala.collection.JavaConverters._
import scala.util.Random

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.pubsub.Pubsub.Builder
import com.google.api.services.pubsub.model.PublishRequest
import com.google.api.services.pubsub.model.PubsubMessage
import com.google.cloud.hadoop.util.RetryHttpInitializer
import com.google.cloud.ServiceOptions

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.pubsub.ConnectionUtils
//import org.apache.spark.streaming.pubsub.PubsubTestUtils
import org.apache.spark.streaming.pubsub.PubsubUtils
import org.apache.spark.streaming.pubsub.SparkGCPCredentials
import org.apache.spark.streaming.pubsub.SparkPubsubMessage
import org.apache.spark.streaming.Milliseconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.SparkConf

object MyProducer extends App {

  def pubMessage : Unit = {
    val projectId = ServiceOptions.getDefaultProjectId
    val topic = "my-topic"
    val recordsPerSecond = "10"

    val APP_NAME = this.getClass.getSimpleName

    val client = new Builder(
      GoogleNetHttpTransport.newTrustedTransport(),
      JacksonFactory.getDefaultInstance,
      new RetryHttpInitializer(
        SparkGCPCredentials.builder.build().provider,
        APP_NAME
      )
    ).setApplicationName(APP_NAME)
      .build()
    //println("a...")
    //val a = GoogleNetHttpTransport.newTrustedTransport()
    //println("b...")
    //val b = JacksonFactory.getDefaultInstance
    //println("cred_provider...")
    //val cp = SparkGCPCredentials.builder
    //  .jsonServiceAccount("/home/arthur/.gcp/cred.json")
    //  .build()
    //  .provider
    //println("c...")
    //val c = new RetryHttpInitializer(cp, APP_NAME)
    //println("client...")
    //val client = new Builder(a, b, c).setApplicationName(APP_NAME).build()
    println("client created")

    val randomWords = List("google", "cloud", "pubsub", "say", "hello")
    val publishRequest = new PublishRequest()
    for (i <- 1 to 10) {
      val messages = (1 to recordsPerSecond.toInt).map { recordNum =>
        val randomWordIndex = Random.nextInt(randomWords.size)
        new PubsubMessage().encodeData(randomWords(randomWordIndex).getBytes()) //.setAttributes()
      }
      publishRequest.setMessages(messages.asJava)
      client
        .projects()
        .topics()
        .publish(s"projects/$projectId/topics/$topic", publishRequest)
        .execute()
      println(s"Published data. topic: $topic; Mesaage: $publishRequest")

      Thread.sleep(1000)
    }
  }

  def pipeline = Unit {
    //val Seq(projectId, subscription) = args.toSeq

    val projectId = ServiceOptions.getDefaultProjectId
    val subscription = "my-topic"
    val recordsPerSecond = "10"

    val APP_NAME = this.getClass.getSimpleName

    //val sparkConf = new SparkConf().setAppName("PubsubWordCount")
    //val ssc = new StreamingContext(sparkConf, Milliseconds(2000))

    val pubsubStream: ReceiverInputDStream[SparkPubsubMessage] = PubsubUtils.createStream(
      ssc, projectId, None, subscription,
      SparkGCPCredentials.builder.build(), StorageLevel.MEMORY_AND_DISK_SER_2)

    val wordCounts =
      pubsubStream.map(message => (new String(message.getData()), 1)).reduceByKey(_ + _)

    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }


  override def main(args: Array[String]): Unit = {

  }
}

