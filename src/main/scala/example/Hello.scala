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
  println(PourcentageFailBecauseOfFuel())
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
      .cache()
  }

  def carsFailsRDD() : RDD[Voiture] = {
    loadData()
      .filter(car => car.isFailing)
  }

  def carsFailsAverageTemperature(): Double = {
    carsFailsRDD
      // rajouter un check is carsFails retourne rien, sinon ça pête
      .map(car => car.engineTemperature)
      .sum / carsFailsRDD.count()
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

  def nbCarsFailingWithNoFuel() : Double = {
    carsFailsRDD()
      .filter(car => car.fuelInTank == 0)
      .count()
  }

  def PourcentageFailBecauseOfFuel(): Double = {
    nbCarsFailingWithNoFuel() / carsFailsRDD().count()
  }


}

trait Greeting {
  lazy val greeting: String = "hello"
}



