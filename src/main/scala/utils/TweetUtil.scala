package utils

import com.google.gson._

object TweetUtil{
  case class Tweet (
                     id : String,
                     user : String,
                     text : String,
                     place : String,
                     country : String,
                     lang : String
                   )


  def parseFromJson(lines:Iterator[String]):Iterator[Tweet] = {
    val gson = new Gson
    lines.map(line => gson.fromJson(line, classOf[Tweet]))
  }
}
