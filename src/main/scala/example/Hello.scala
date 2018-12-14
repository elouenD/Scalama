package example

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import utils.VoitureUtils
import utils.VoitureUtils.Voiture


object Hello extends Greeting with App {
  println(greeting)

  val pathToFile = "data/reduced-voiture.json"
  val PARISLATITUDEMIN = 48.806555
  val PARISLONGITUDEMIN = 2.239097
  val SFLATITUDEMIN = 37.696036
  val SFLONGITUDEMIN = -122.509512

  val PARISLATITUDEMAX = 48.903012
  val PARISLONGITUDEMAX = 2.419856
  val SFLATITUDEMAX = 37.814928
  val SFLONGITUDEMAX = -122.374243

  val test = loadData()
  println("test")
  println(CarsIsFailingMoreInParis())
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

  /**
    *
    * @return list de voiture qui fail
    */
  def carsFailsRDD() : RDD[Voiture] = {
    loadData()
      .filter(car => car.isFailing)
  }

  /**
    *
    * @return temperature moyenne des voitures qui fail
    */
  def carsFailsAverageTemperature(): Double = {
    carsFailsRDD
      // rajouter un check is carsFails retourne rien, sinon ça pête
      .map(car => car.engineTemperature)
      .sum / carsFailsRDD.count()
  }

  /**
    *
    * @return array of cars moving
    */
  def carsMovingRDD() : RDD[Voiture] = {
    loadData()
      .filter(car => car.isMoving)
  }

  /**
    *
    * @return average temperatur for moving cars
    */
  def carsMovingAverageTemperature(): Double = {
    val carsMovingRDD = carsFailsRDD()
    carsMovingRDD
      .map(car => car.engineTemperature)
      .sum / carsMovingRDD.count()
  }

  /**
    *
    * @return is the temperatur for failing cars is higher
    */
  def CheckTempatureFailsCarsIsHigher(): Boolean = {
    carsFailsAverageTemperature() > carsMovingAverageTemperature()
  }

  // question 4
  /**
    *
    * @return nombre de voiture en fail à cause du carburant
    */
  def nbCarsFailingWithNoFuel() : Double = {
    carsFailsRDD()
      .filter(car => car.fuelInTank == 0)
      .count()
  }

  /**
    *
    * @return le pourcentage de voiture qui fail à cause du carburant
    */
  def PourcentageFailBecauseOfFuel(): Double = {
    nbCarsFailingWithNoFuel() / carsFailsRDD().count()
  }

  // question 3
  /**
    *
    * @return nombre de cars qui fail à Paris
    */
  def nbCarsFailingInParis(): Double = {
    carsFailsRDD()
      .filter(cars => (PARISLATITUDEMIN <= cars.lat && cars.lat <= PARISLATITUDEMAX )
        && (PARISLONGITUDEMIN <= cars.long && cars.long <= PARISLONGITUDEMAX)
      )
      .count()
  }

  /**
    *
    * @return nombre de cars qui fail à San Fransisco
    */
  def nbCarsFailingInSanFransisco(): Double = {
    carsFailsRDD()
      .filter(cars => (SFLATITUDEMIN <= cars.lat && cars.lat <=  SFLATITUDEMAX )
        && (SFLONGITUDEMIN <= cars.long && cars.long <= SFLONGITUDEMAX)
      )
      .count()
  }


  /**
    *
    * @return true si il y a plus de cars qui fail à Paris, sinon false
    */
  def CarsIsFailingMoreInParis(): Boolean = {
    nbCarsFailingInParis() > nbCarsFailingInSanFransisco()
  }



}

trait Greeting {
  lazy val greeting: String = "hello"
}



