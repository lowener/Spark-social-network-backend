package socialMedia.backend

import scala.collection.JavaConverters._
import scala.util.Random

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.pubsub.Pubsub.Builder
import com.google.api.services.pubsub.model.PublishRequest
import com.google.api.services.pubsub.model.Subscription
import com.google.api.services.pubsub.model._
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
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.Time



object MyProducer extends App {


  def pubItem(item: Item): Unit = {
    val APP_NAME = this.getClass.getSimpleName
    val projectId = ServiceOptions.getDefaultProjectId
    val client = new Builder(
      GoogleNetHttpTransport.newTrustedTransport(),
      JacksonFactory.getDefaultInstance,
      new RetryHttpInitializer(
        SparkGCPCredentials.builder.build().provider,
        APP_NAME
      )
    ).setApplicationName(APP_NAME)
      .build()


    val message = new PubsubMessage().encodeData(item.getMessage.getBytes()).setAttributes(item.getAttributes.asJava)
    val publishRequest = new PublishRequest().setMessages(List(message).asJava)
    client
      .projects()
      .topics()
      .publish(s"projects/$projectId/topics/${item.getTopic}", publishRequest)
      .execute()
    println(s"Published data. topic: ${item.getTopic}; Message: $publishRequest")
  }

  def multiPub(listItem: List[Item]): Unit = {
    val projectId = ServiceOptions.getDefaultProjectId
    val client = new Builder(
      GoogleNetHttpTransport.newTrustedTransport(),
      JacksonFactory.getDefaultInstance,
      new RetryHttpInitializer(
        SparkGCPCredentials.builder.build().provider,
        "publish"
      )
    ).setApplicationName("publish")
      .build()

    val messages = listItem.map { item =>
      new PubsubMessage().encodeData(item.getMessage.getBytes()).setAttributes(item.getAttributes.asJava)
    }
    val publishRequest = new PublishRequest().setMessages(messages.asJava)
    client
      .projects()
      .topics()
      .publish(s"projects/$projectId/topics/${listItem.head.getTopic}", publishRequest)
      .execute()
    println(s"Published data. topic: ${listItem.head.getTopic}; Message: $publishRequest")
  }

  def randomString(length: Int): String = Random.alphanumeric.take(length).mkString

  def Occurences(word: String, subscription: String) = {
    val projectId = ServiceOptions.getDefaultProjectId

    val sparkConf = new SparkConf().setAppName("PubsubWordCount").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Milliseconds(2000))

    val pubsubStream: ReceiverInputDStream[SparkPubsubMessage] = PubsubUtils.createStream(
      ssc, projectId, None, subscription,
      SparkGCPCredentials.builder.build(),StorageLevel.MEMORY_ONLY)

    val wordOccurences =
      pubsubStream
        .map(_.getData())
        .flatMap(_.toString.split(" "))
        .filter(_.toLowerCase.contains(word)).count()//.compute(Time(30000))
    wordOccurences.print()

    ssc.start()
    ssc.awaitTermination()
    ssc.stop()
    //val resCount = wordOccurences match {
    //  case None => 0
    //  case Some(rdd) => rdd.count()
    //}
    //resCount
    wordOccurences
  }


  override def main(args: Array[String]): Unit = {
    if (args(0).equals("publish")) {
      val postLeno = new Post("les pc de apple sont tres tres mauvais", "Arthur")
      pubItem(postLeno)
      val mes = new SocialMessage(randomString(6), randomString(6), randomString(15))
      pubItem(mes)
      val user = new User(randomString(6), Random.nextInt(80))
      pubItem(user)
      val post = new Post(randomString(15), randomString(6))
      pubItem(post)
    }
    if (args(0).equals("sub")) {
      val occurencesPost = Occurences(args(1).toLowerCase, "sub-get-post")
      val occurencesMessage = Occurences(args(1).toLowerCase, "sub-get-message")
      //println(s"${args(1)} appears ${occurencesMessage + occurencesPost} times")
      occurencesMessage.print()
      occurencesPost.print()
    }
  }
}

