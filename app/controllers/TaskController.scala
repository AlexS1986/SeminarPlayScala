package controllers



import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import actors.StatsActor
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import models.{AccessData, Task}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import services.{AuthService, FootballService, SunService, UserLoginData, WeatherService}
import play.api.data.Form
import play.api.data.Forms._
import user.{UserAuthAction, UserAuthRequest}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class TaskController (controllerComponents: ControllerComponents,
                     sunService: SunService,
                      weatherService: WeatherService,
                     footballService: FootballService,
                     actorSystem: ActorSystem,
                     authService: AuthService,
                     userAuthAction: UserAuthAction)
  extends AbstractController(controllerComponents) {

  val userDataForm = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(UserLoginData.apply)(UserLoginData.unapply)
  }

  //val sunService = new SunService(ws)
  //val weatherService = new WeatherService(ws)
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action {
    implicit request: Request[AnyContent] =>
    Redirect(routes.TaskController.tasks())  
  }

  def tasks = Action {
    implicit request : Request[AnyContent] =>
    Ok(views.html.index(Task.all,getTime))
  }

  def newTask = Action(parse.tolerantFormUrlEncoded) {
    implicit request =>
    Task.add(request.body.get("taskName").get.head)
    Redirect(routes.TaskController.index)
  }

  def deleteTask(id: Int) = Action {
    Task.delete(id)
    Redirect(routes.TaskController.index)
  }

  def getTime = {
    import java.text.SimpleDateFormat
    import java.util.Date

    val date = new Date()
    new SimpleDateFormat().format(date)
  }

  def weather() = Action.async{
    val lat = 51.3671
    val lon = 7.4633

    val sunInfoF = sunService.getSunInfo(lat,lon)
    val temperatureF = weatherService.getTemperature(lat, lon)

    for {
      sunInfo <- sunInfoF
      temperature <- temperatureF
    } yield {
      Ok(views.html.weather(sunInfo, temperature))
    }
  }



  def footballTable() = Action.async{
    val tableF = footballService.getTable()
    for {
      table <- tableF
    } yield {
      Ok(views.html.table(table))
    }
  }

  def accessData() = Action.async{
    val accessDataF = getAccessDataOfLastRequests()
    for {
      accessData <- accessDataF
    } yield {
      Ok(views.html.accessData(accessData))
    }
  }

  def getAccessDataOfLastRequests() = {
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)
    val requestsF = (actorSystem.actorSelection(StatsActor.path) ? StatsActor.GetAccessData).mapTo[Seq[AccessData]]
    requestsF
  }


  def priv() = userAuthAction {
    userRequest: UserAuthRequest[AnyContent] =>
      Ok(views.html.priv(userRequest.user))
  }

/*
  private def extractUser(req: RequestHeader): Option[User] = {

    val sessionTokenOpt = req.session.get("sessionToken")

    println(sessionTokenOpt)

    sessionTokenOpt
      .flatMap(token => Session.getSession(token))
      .filter(_.expiration.isAfter(LocalDateTime.now()))
      .map(_.username)
      .flatMap(UserDAO.getUser)
  } */

 /* def priv() = userAction { userRequest: UserRequest[AnyContent] =>Ok(views.html.priv(userRequest.user.get))
  } */

  /*
  def login_request() = userAction {
    userRequest: UserRequest[AnyContent] => {
      println("Body: " + userRequest.body)

      // not nice do better
      val list1 = userRequest.body.asFormUrlEncoded.get.get("username").get(0)
      val list2 = userRequest.body.asFormUrlEncoded.get.get("password").get(0)


      Redirect(routes.TaskController.login(list1, list2))
      //Redirect(routes.TaskController.login(list1, list2))
      /*userRequest.body.asFormUrlEncoded match {
        case Some(map) => {
          map.get("")
        }
      } */
      //Redirect(routes.TaskController.login("hans", "peter"))
    }
  }*/

  def login_page = Action {
    implicit request =>
    Ok(views.html.login_page(None))
  }

  /*def login(username: String, pass: String) = Action { implicit request: Request[AnyContent] =>
    if (isValidLogin(username, pass)) {
      val token = Session.generateToken(username)

      Redirect(routes.TaskController.priv()).withSession(request.session + ("sessionToken" -> token))
    } else {
      Ok(views.html.login_page())
    }
  }

  private def isValidLogin(username: String, password: String): Boolean = {
    UserDAO.getUser(username).exists(_.password == password)
  } */


  def doLogin = Action {
    implicit request =>
      userDataForm.bindFromRequest.fold(
        formWithErrors => Ok(views.html.login_page(Some("Wrong data"))),
        userLoginData => {
          val cookieOption = authService.login(userLoginData.username, userLoginData.password)
          cookieOption match {
            case Some(cookie) => Redirect("/priv").withCookies(cookie)
            case None =>
                Ok(views.html.login_page(Some("Login failed")))
          }

    }
      )
  }

}
