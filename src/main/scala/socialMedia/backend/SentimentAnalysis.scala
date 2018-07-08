package socialMedia.backend

import scala.collection.JavaConverters._
import scala.util.Random

import org.apache.spark.storage.StorageLevel
import org.apache.spark.broadcast.Broadcast

import org.apache.hadoop.io.compress.GzipCodec

import org.apache.spark.sql._
import org.apache.spark.rdd._
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkConf
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.sql.types._
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer, VectorAssembler, LabeledPoint}
import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.linalg._
import scala.reflect.runtime.universe._

object SentimentAnalysis {
   def getType[T: TypeTag](value: T) = typeOf[T]
   /*import SparkSession.implicits._
      import spark.implicits._*/
   Logger.getLogger("org").setLevel(Level.OFF)
   Logger.getLogger("akka").setLevel(Level.OFF)


   def sanitizeString(text: String, stopWordsList: List[String]) : String = {
      text.toLowerCase()
         .replaceAll("\n", "")
         .replaceAll("rt\\s+", "")
         .replaceAll("\\s+@\\w+", "")
         .replaceAll("@\\w+", "")
         .replaceAll("\\s+#\\w+", "")
         .replaceAll("#\\w+", "")
         .replaceAll("(?:https?|http?)://[\\w/%.-]+", "")
         .replaceAll("(?:https?|http?)://[\\w/%.-]+\\s+", "")
         .replaceAll("(?:https?|http?)//[\\w/%.-]+\\s+", "")
         .replaceAll("(?:https?|http?)//[\\w/%.-]+", "")
         .split("\\W+")
         .filter(_.matches("^[a-zA-Z]+$"))
         .filter(!stopWordsList.contains(_))
         .mkString
   }
   def main(args: Array[String]) {
      /*val conf = new SparkConf().setAppName("SentimentAnalysis").setMaster("local[*]")
      val sc = SparkContext.getOrCreate(conf)
      */
      val sc = SparkSession.builder.appName("SentimentAnalysis").master("local[*]").getOrCreate()
      val sparkCtxt = sc.sparkContext
      import sc.implicits._
      //val data = sparkSession.read.text("src/main/resources/data.txt").as[String]
      val stopwords = sc.read.text("nltk_stopwords.txt").as[String].flatMap(_.split('\n')).collect.toList
      val tweetsDF : DataFrame = sc.read
                     .format("com.databricks.spark.csv")
                     .option("header", "false")
                     .option("inferSchema", "true")
                     .load("training.csv")
                     .toDF("label", "id", "date", "query", "user", "text")
                     .drop("id")
                     .drop("date")
                     .drop("query")
                     .drop("user")

      val hashingTF = new HashingTF().setInputCol("words").setOutputCol("rawFeatures")

      val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
      val wordsData = tokenizer.transform(tweetsDF)
      val featurizedData = hashingTF.transform(wordsData).select($"label", $"rawFeatures").rdd.map{
         case Row(label: Int, feats: Vector) =>
            LabeledPoint(label, feats)
         case e => LabeledPoint(e(0).asInstanceOf[Double], e(1).asInstanceOf[Vector])
      }
      val model : NaiveBayesModel = new NaiveBayes().fit(featurizedData.toDF)
      model.save("naiveBayes.model")

      featurizedData.foreach(println)
      /*val naiveBayesModel: NaiveBayesModel = NaiveBayes.train(labelledRDD, lambda = 1.0, modelType = "multinomial")
      naiveBayesModel.save(sc.sparkContext, "naiveBayes.model")*/
   }
}
