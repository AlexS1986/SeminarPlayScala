package filters

import akka.stream.Materializer
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}
import actors.StatsActor
import akka.actor.ActorSystem
import models.AccessData
import scala.concurrent.Future

class StatsFilter(actorSystem: ActorSystem,
                  implicit val mat: Materializer) extends Filter {
  override def apply(nextFilter: (RequestHeader) => Future[Result])
                    (header: RequestHeader): Future[Result] = {
                        Logger("play").info(s"Serving another request: ${header.path}")

                        import java.text.SimpleDateFormat
                        import java.util.Date
                        val date = new Date()
                        actorSystem.actorSelection(StatsActor.path) !
                          StatsActor.RequestReceived(AccessData(new SimpleDateFormat().format(date), header.remoteAddress))
                        nextFilter(header)
  }
}