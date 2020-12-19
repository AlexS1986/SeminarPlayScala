package services

import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

import models.SunInfo

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.ws.WSClient

import scala.concurrent.Future

class SunService(wsClient: WSClient) {
  def getSunInfo(lat: Double, lon: Double):Future[SunInfo] = {
    val responseF  = wsClient.url(s"http://api.sunrise-sunset.org/json?lat=$lat" +
      s"&lng=$lon&formatted=0").get()
    val myresponseFuture = responseF.map {
      response =>
        val json = response.json
        val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
        val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
        val sunriseTime = ZonedDateTime.parse(sunriseTimeStr)
        val sunsetTime = ZonedDateTime.parse(sunsetTimeStr)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Europe/Paris"))
        val sunInfo = SunInfo(sunriseTime.format(formatter), sunsetTime.format(formatter))
        sunInfo
    }
    myresponseFuture
  }

}
