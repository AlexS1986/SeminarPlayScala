import controllers.TaskController
import play.api.ApplicationLoader.Context
import play.api._
import play.api.Logger
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import router.Routes
import play.api.routing.Router
import com.softwaremill.macwire._
import _root_.controllers.AssetsComponents
import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.Props
import filters.StatsFilter
import play.api.cache.ehcache.EhCacheComponents
import play.filters.HttpFiltersComponents
import play.filters.csrf._
import services.{AuthService, FootballService, SunService, WeatherService}
import scalikejdbc.config.DBs
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.db.DBComponents
import play.api.db.HikariCPComponents
import user.UserAuthAction

import scala.concurrent.Future

class AppApplicationLoader extends ApplicationLoader {
  def load(context: Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment, context.initialConfiguration, Map.empty)
    }
    new AppComponents(context).application

    /*LoggerConfigurator(context.environment.classLoader).foreach { cfg => cfg.configure(context.environment)
    }
    new AppComponents(context).application */
  }
}

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with AhcWSComponents
  with EvolutionsComponents with DBComponents with HikariCPComponents with AssetsComponents with HttpFiltersComponents  with CSRFComponents with EhCacheComponents {

  override lazy val controllerComponents = wire[DefaultControllerComponents]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController = wire[TaskController]

  // userAction
  //lazy val bodyParser = wire[BodyParsers.Default]
  lazy val userAuthAction = wire[UserAuthAction]


  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]
  lazy val footballService = wire[FootballService]

  lazy val statsFilter: Filter = wire[StatsFilter]

  override lazy val dynamicEvolutions = new DynamicEvolutions // Database Modern Web Development with Scala p. 114

  override lazy val httpFilters = Seq(csrfFilter,statsFilter) // csrfFilter in CSRFComponents

  lazy val statsActor = actorSystem.actorOf(Props(wire[StatsActor]), StatsActor.name) // es gib nun eine Instanz von StatsActor mit name StatsActor.name im actorSystem


  lazy val authService = new AuthService(defaultCacheApi.sync) // EhCache authentification

  applicationLifecycle.addStopHook{
        //logging
        val loggerINFOLevel = Logger("play")
        () => loggerINFOLevel.info("Application is about to stop.")

          // DB
          //
          applicationEvolutions // calls evolution script DB
          DBs.closeAll()

        Future.successful()
  }

  val onStart = {
    val loggerINFOLevel = Logger("play")
    loggerINFOLevel.info("Application is about to start")
    DBs.setupAll()
    statsActor ! Ping
  }
}

