package actors
import actors.StatsActor.{GetAccessData, Ping, RequestReceived}
import akka.actor.Actor
import models.AccessData

import scala.collection.mutable

class StatsActor extends Actor {
  var accessDataOfLastRequests = mutable.Queue[AccessData]()

  override def receive: Receive = {
    case Ping => ()
    case RequestReceived(accessData) => {
      accessDataOfLastRequests = accessDataOfLastRequests.enqueue(accessData)
      if (accessDataOfLastRequests.length>10) {
        accessDataOfLastRequests.removeHead()
      }
    }
    case GetAccessData => sender() ! accessDataOfLastRequests.toSeq
    case _ => println("Message not implemented!")
  }
}

object StatsActor {
  val name = "statsActor"
  val path = s"/user/$name"
  case object Ping
  case object GetAccessData
  case class RequestReceived(accessData: AccessData)
}
