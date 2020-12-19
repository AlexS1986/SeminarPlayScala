package services

import models.FootballClub
import play.api.libs.json.{JsArray, JsDefined, JsValue}
import play.api.libs.ws._
import play.api.libs.ws.ahc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class FootballService(wsClient: WSClient) {

  def getTable(): Future[Seq[FootballClub]] = {
    val footballRequest = wsClient.url("https://www.openligadb.de/api/getbltable/bl1/2020")
    val footballResponseF : Future[AhcWSResponse] = footballRequest.get().asInstanceOf[Future[AhcWSResponse]]

    val myResponseFuture = footballResponseF.map {
      footballResponse => {
        val asJson = footballResponse.body[JsValue].asInstanceOf[JsArray]
        val list   = asJson.value map { jValue => ((jValue \ "ShortName"), (jValue \ "TeamIconUrl"))  match {
          case (JsDefined(jsName), JsDefined(jsPic)) => FootballClub(jsName.toString().replaceAll("\u0022", ""), jsPic.toString().replaceAll("\u0022", ""))
        }
        }
        list.toSeq
      }
    }
    myResponseFuture
  }
}

