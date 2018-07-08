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
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer, VectorAssembler}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg._

object Utils {
   // FIXME: Use a sparkContext
   def asarray(s: Seq[String]) : Array[String] = {
      s.toArray
   }

   def asdf(s: Seq[String]) : DataFrame = {
      try {
         val sc = SparkSession.builder.appName("SentimentAnalysis").master("local[*]").getOrCreate()
         val sparkCtxt = sc.sparkContext
         import sc.implicits._
         s.toDF
      } catch {
        case e: Exception => DataFrame()
      }

   }
}

object SentimentAnalysis {
   /*import SparkSession.implicits._
      import spark.implicits._*/
   Logger.getLogger("org").setLevel(Level.OFF)
   Logger.getLogger("akka").setLevel(Level.OFF)


   def sanitizeString(text: String, stopWordsList: List[String]) : Seq[String] = {
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

      val hashingTF = new HashingTF().setOutputCol("rawFeatures")

      val labelledRDD = tweetsDF.select("label", "text").rdd.map {
         case Row(label: Int, text: String) => {
            val simplerText = text.replaceAll("\n", "")
            val textSanitized : Seq[String] = sanitizeString(simplerText, stopwords)
            println(textSanitized)
            val myArr : Array[String] = Utils.asarray(textSanitized)
            //println(Vectors.dense(textSanitized.toArray))


            //val hashedTF : DataFrame = hashingTF.transform((textSanitized))
            //println(hashedTF.collect.toArray.size)
            val myArr2 = hashedTF.collect.map(_.size)
            try {
              // ...
              println(myArr2.size)
            } catch {
              case e: Exception =>
            }
            myArr2
            // ->val vecFeatures: Vector = Vectors.dense()
            //val vecFeatures: Vector = new VectorAssembler().transform(hashedTF)
            // ->LabeledPoint(label, vecFeatures)
            /*val hashedTF = hashingTF.transform(textSanitized)
            (score.toDouble,
             model.predict(hashedTF),
             simplerText)*/
         }
      }
      labelledRDD.foreach(println)
      /*val naiveBayesModel: NaiveBayesModel = NaiveBayes.train(labelledRDD, lambda = 1.0, modelType = "multinomial")
      naiveBayesModel.save(sc.sparkContext, "naiveBayes.model")*/
   }
}
