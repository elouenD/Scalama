package utils

import com.google.gson._

object VoitureUtils{
  case class Voiture (
                     lat : Float,
                     long : Float,
                     vehiculeId : String,
                     isFailing : Boolean,
                     temperature : Int,
                     engineTemperature : Int,
                     fuelInTank: Int,
                     isMoving: Boolean,
                     timestamp: Int
                   )




  def parseFromJson(lines:Iterator[String]):Iterator[Voiture] = {
    val gson = new Gson
    lines.map(line => gson.fromJson(line, classOf[Voiture]))
  }
}
