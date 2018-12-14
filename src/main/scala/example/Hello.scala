package example

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import utils.VoitureUtils
import utils.VoitureUtils.Voiture


object Hello extends Greeting with App {
  println(greeting)

  val pathToFile = "data/reduced-voiture.json"

  val test = loadData()
  println("test")
  println(test.map(car => car.isFailing))
 println(carsFailsAverageTemperature())
  println("test")


  def loadData(): RDD[Voiture] = {
    // Create the spark configuration and spark context
    val conf = new SparkConf()
      .setAppName("User mining")
      .setMaster("local[*]")

    val sc = SparkContext.getOrCreate(conf)

    // Load the data and parse it into a Tweet.
    // Look at the Tweet Object in the TweetUtils class.
    sc.textFile(pathToFile)
      .mapPartitions(VoitureUtils.parseFromJson(_))
  }

  def carsFailsRDD() : RDD[Voiture] = {
    loadData()
      .filter(car => car.isFailing)
  }

  def carsFailsAverageTemperature(): Double = {
    val carsFails = carsFailsRDD()
    carsFails
      .map(car => car.engineTemperature)
      .sum / carsFails.count()
  }

  def carsMovingRDD() : RDD[Voiture] = {
    loadData()
      .filter(car => car.isMoving)
  }

  def carsMovingAverageTemperature(): Double = {
    val carsMovingRDD = carsFailsRDD()
    carsMovingRDD
      .map(car => car.engineTemperature)
      .sum / carsMovingRDD.count()
  }

  def CheckTempatureFailsCarsIsHigher(): Boolean = {
    carsFailsAverageTemperature() > carsMovingAverageTemperature()
  }

}

trait Greeting {
  lazy val greeting: String = "hello"
}



