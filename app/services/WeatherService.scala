package services

import play.api.libs.ws._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WeatherService(wsClient: WSClient) {
  def getTemperature(lat: Double, lon: Double): Future[Double] = {
    val weatherResponseF = wsClient.url("http://api.openweathermap.org/data/2.5/" +
      s"weather?lat=$lat&lon=$lon&units=metric&appID=514f83357773b8259f45c9dac3890ca3").get()
    val myResponseFuture = weatherResponseF.map{
      weatherResponse =>
        val weatherJson = weatherResponse.json
        val temperature = (weatherJson \ "main" \ "temp").as[Double]
        temperature
    }
    myResponseFuture
  }
}

